package ru.practicum.events.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.StatsClient;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.dto.StatsDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventsState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.model.Location;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.dto.RequestStatusUpdateDto;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UsersRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final UsersRepository usersRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private ObjectMapper mapper;

    @Override
    public EventFullDto getEventById(Long id) {
        Optional<Event> eventOpt = eventRepository.getByIdAndState(id, EventsState.PUBLISHED);
        if (eventOpt.isEmpty()) {
            throw new NoObjectException(String.format("Published event with id = %s was not found", id));
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventOpt.get());
        eventFullDto.setViews(getEventViews(List.of(id)).get(id));
        //TODO getConfirmedRequests
        return eventFullDto;
    }

    @Override
    public List<EventShortDto> getEvents(String text, Integer[] categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        List<Event> events;
        Boolean[] paids;
        if (paid == null) {
            paids = new Boolean[]{true, false};
        } else {
            paids = new Boolean[]{paid};
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(3000, 1, 1, 0, 0).format(DATE_TIME_FORMATTER);
        }
        if ((categories == null || categories.length == 0) && !onlyAvailable) {
            events = eventRepository.getForPublic(text, paids, rangeStart, rangeEnd, PageRequest.of(from / size, size)).getContent();
        } else if ((categories == null || categories.length == 0)) {
            events = eventRepository.getForPublicWithLimit(text, paids, rangeStart, rangeEnd, PageRequest.of(from / size, size)).getContent();
        } else if (!onlyAvailable) {
            events = eventRepository.getForPublicWithCategories(text, categories, paids, rangeStart, rangeEnd, PageRequest.of(from / size, size)).getContent();
        } else {
            events = eventRepository.getForPublicWithCategoriesAndLimit(text, categories, paids, rangeStart, rangeEnd, PageRequest.of(from / size, size)).getContent();
        }
        List<EventShortDto> eventShortDtos = EventMapper.toEventShortDtoList(events);
        List<Long> eventsIds = eventShortDtos.stream().map(EventShortDto::getId).collect(Collectors.toList());
        Map<Long, Long> eventViews = getEventViews(eventsIds);
        for (EventShortDto eventShortDto : eventShortDtos) {
            eventShortDto.setViews(eventViews.get(eventShortDto.getId()));
        }
        //TODO getConfirmedRequests
        return eventShortDtos;
    }

    @Override
    public List<EventShortDto> getEventsByUser(Integer userId) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        List<Event> events = eventRepository.getByInitiatorId(userId);
        List<EventShortDto> eventShortDtos = events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        List<Long> eventsIds = eventShortDtos.stream().map(EventShortDto::getId).collect(Collectors.toList());
        Map<Long, Long> eventViews = getEventViews(eventsIds);
        for (EventShortDto eventShortDto : eventShortDtos) {
            eventShortDto.setViews(eventViews.get(eventShortDto.getId()));
        }
        //TODO getConfirmedRequests
        return eventShortDtos;
    }

    @Override
    public EventFullDto createEvent(Integer userId, NewEventDto newEventDto) {
        Optional<User> userOpt = usersRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(categoryRepository.findById(newEventDto.getCategory()).get());
        event.setInitiator(userOpt.get());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventsState.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto patchEvent(Integer userId, NewEventDto newEventDto, Long eventId) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NoObjectException(String.format("Event with id = %s was not found", eventId));
        }
        if (!Objects.equals(eventOpt.get().getInitiator().getId(), userId)) {
            throw new ValidationException(String.format("Event with id = %s was not create by user with id = %s", eventId, userId));
        }
        Event newEvent = EventMapper.toEvent(newEventDto);
        Event event = updateEvent(eventOpt.get(), newEvent);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(getEventViews(List.of(eventId)).get(eventId));
        //TODO getConfirmedRequests
        return eventFullDto;
    }

    @Override
    public EventFullDto getEventForCreator(Integer userId, Long eventId) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NoObjectException(String.format("Event with id = %s was not found", eventId));
        }
        if (!Objects.equals(eventOpt.get().getInitiator().getId(), userId)) {
            throw new ValidationException(String.format("Event with id = %s was not create by user with id = %s", eventId, userId));
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventOpt.get());
        eventFullDto.setViews(getEventViews(List.of(eventId)).get(eventId));
        //TODO getConfirmedRequests
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long eventId, Integer userId) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        if (eventRepository.existsById(eventId)) {
            throw new NoObjectException(String.format("Event with id = %s was not found", eventId));
        }
        return RequestMapper.toParticipationRequestDtoList(requestRepository.findByEvent(eventId));
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestsStatus(Long eventId, Integer userId, RequestStatusUpdateDto requestStatusUpdateDto) {
        return null;
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(Integer[] categories, Integer[] users, String[] states, String rangeStart, String rangeEnd, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto patchEventForAdmin(Long eventId, NewEventDto newEventDto) {
        return null;
    }

    private Map<Long, Long> getEventViews(List<Long> events) {
        String[] uris = (String[]) events.stream().map(event -> String.format("/events/%s", event)).toArray();
        ResponseEntity<Object> eventStatEntity = statsClient.getStats(LocalDateTime.of(2020, 1, 1, 0, 0).format(DATE_TIME_FORMATTER),
                LocalDateTime.of(2030, 1, 1, 0, 0).format(DATE_TIME_FORMATTER),
                uris, false);
        List<StatsDto> eventStats = mapper.convertValue(eventStatEntity, new TypeReference<>() {
        });

        Map<Long, Long> eventViews = new HashMap<>();
        for (StatsDto eventStat : eventStats) {
            Long eventId = Long.parseLong(eventStat.getUri().replace("/events/", ""));
            eventViews.put(eventId, eventStat.getHits());
        }
        return eventViews;
    }

    private Event updateEvent(Event event, Event newEvent) {
        if (newEvent.getAnnotation() != null && !newEvent.getAnnotation().equals(event.getAnnotation())) {
            event.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getCategory() != null && !newEvent.getCategory().equals(event.getCategory())) {
            if (categoryRepository.existsById(newEvent.getCategory().getId())) {
                event.setCategory(newEvent.getCategory());
            }
        }
        if (newEvent.getDescription() != null && !newEvent.getDescription().equals(event.getDescription())) {
            event.setDescription(newEvent.getDescription());
        }
        if (newEvent.getEventDate() != null && !newEvent.getEventDate().equals(event.getEventDate())) {
            event.setEventDate(newEvent.getEventDate());
        }
        Location existLocation = new Location(event.getLocationLat(), event.getLocationLon());
        Location newLocation = new Location(newEvent.getLocationLat(), newEvent.getLocationLon());
        if (!newLocation.equals(existLocation)) {
            event.setLocationLat(newLocation.getLat());
            event.setLocationLon(newLocation.getLon());
        }
        if (newEvent.getParticipantLimit() != null && !newEvent.getParticipantLimit().equals(event.getParticipantLimit())) {
            event.setParticipantLimit(newEvent.getParticipantLimit());
        }
        if (newEvent.getPaid() != null && !newEvent.getPaid().equals(event.getPaid())) {
            event.setPaid(newEvent.getPaid());
        }
        if (newEvent.getTitle() != null && !newEvent.getTitle().equals(event.getTitle())) {
            event.setTitle(newEvent.getTitle());
        }
        if (newEvent.getRequestModeration() != null && !newEvent.getRequestModeration().equals(event.getRequestModeration())) {
            event.setRequestModeration(newEvent.getRequestModeration());
        }
        return event;
    }
}
