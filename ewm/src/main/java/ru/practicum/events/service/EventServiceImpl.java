package ru.practicum.events.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
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
import ru.practicum.requests.dto.RequestStatusUpdateDto;
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
        return null;
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
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        Event event = EventMapper.toEvent(newEventDto);
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
        Event event = updateEvent(eventOpt.get(),newEventDto);
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
        return null;
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

    private Event updateEvent(Event event, NewEventDto newEventDto){
        if (newEventDto.getAnnotation() != null && !newEventDto.getAnnotation().equals(event.getAnnotation())) {
            event.setAnnotation(newEventDto.getAnnotation());
        }
        if (newEventDto.getCategory() != null && !newEventDto.getCategory().equals(event.getCategory().getId())) {
            if (categoryRepository.existsById(newEventDto.getCategory())){
                event.setCategory(categoryRepository.findById(newEventDto.getCategory()).get());
            }
        }
        if (newEventDto.getDescription() != null && !newEventDto.getDescription().equals(event.getDescription())) {
            event.setDescription(newEventDto.getDescription());
        }
        if (newEventDto.getEventDate() != null && !newEventDto.getEventDate().equals(event.getEventDate().format(DATE_TIME_FORMATTER))){
            event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(),DATE_TIME_FORMATTER));
        }
        Location existLocation = new Location(event.getLocation_lat(),event.getLocation_lon());
        if (newEventDto.getLocation() != null && !newEventDto.getLocation().equals(existLocation)){
            event.setLocation_lat(newEventDto.getLocation().getLat());
            event.setLocation_lon(newEventDto.getLocation().getLon());
        }
        if (newEventDto.getParticipantLimit() != null && !newEventDto.getParticipantLimit().equals(event.getParticipantLimit())){
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        if (newEventDto.getPaid() != null && !newEventDto.getPaid().equals(event.getPaid())){
            event.setPaid(newEventDto.getPaid());
        }
        if (newEventDto.getTitle() != null && !newEventDto.getTitle().equals(event.getTitle())){
            event.setTitle(newEventDto.getTitle());
        }
        if (newEventDto.getRequestModeration() != null && !newEventDto.getRequestModeration().equals(event.getRequestModeration())){
            event.setRequestModeration(newEventDto.getRequestModeration());
        }
        return event;
    }
}
