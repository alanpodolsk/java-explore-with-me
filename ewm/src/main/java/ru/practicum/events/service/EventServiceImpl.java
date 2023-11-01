package ru.practicum.events.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.comments.dto.CommentMapper;
import ru.practicum.comments.dto.ShortCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.dto.StatsDto;
import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventsState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.dto.RequestStatusUpdateDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestJdbcRepository;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UsersRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.comments.model.CommentState.PUBLISHED;

@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final UsersRepository usersRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestJdbcRepository requestJdbcRepository;
    private final CommentRepository commentRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private ObjectMapper mapper;

    @Override
    public EventFullDto getEventById(Long id) {
        Optional<Event> eventOpt = eventRepository.getByIdAndState(id, EventsState.PUBLISHED);
        if (eventOpt.isEmpty()) {
            throw new NoObjectException(String.format("Published event with id = %s was not found", id));
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventOpt.get());
        return fullEventsEnrichment(List.of(eventFullDto)).get(0);
    }

    @Override
    public List<EventShortDto> getEvents(String text, Integer[] categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size) {
        List<Event> events;
        Boolean[] paids;
        LocalDateTime start;
        LocalDateTime end;
        if (paid == null) {
            paids = new Boolean[]{true, false};
        } else {
            paids = new Boolean[]{paid};
        }
        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
        }
        if (rangeEnd == null) {
            end = LocalDateTime.of(3000, 1, 1, 0, 0);
        } else {
            end = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
        }
        if ((categories == null || categories.length == 0) && !onlyAvailable) {
            events = eventRepository.getForPublic('%' + text + '%', Arrays.asList(paids), start, end, PageRequest.of(from / size, size)).getContent();
        } else if ((categories == null || categories.length == 0)) {
            events = eventRepository.getForPublicWithLimit('%' + text + '%', Arrays.asList(paids), start, end, PageRequest.of(from / size, size)).getContent();
        } else if (!onlyAvailable) {
            events = eventRepository.getForPublicWithCategories('%' + text + '%', Arrays.asList(categories), Arrays.asList(paids), start, end, PageRequest.of(from / size, size)).getContent();
        } else {
            events = eventRepository.getForPublicWithCategoriesAndLimit('%' + text + '%', Arrays.asList(categories), Arrays.asList(paids), start, end, PageRequest.of(from / size, size)).getContent();
        }
        List<EventShortDto> eventShortDtos = EventMapper.toEventShortDtoList(events);
        if (!eventShortDtos.isEmpty()) {
            return shortEventsEnrichment(eventShortDtos);
        } else {
            throw new ValidationException("Events must be published");
        }
    }

    @Override
    public List<EventShortDto> getEventsByUser(Integer userId, Integer from, Integer size) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        List<Event> events = eventRepository.getByInitiatorId(userId, PageRequest.of(from / size, size)).getContent();
        List<EventShortDto> eventShortDtos = events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        if (!eventShortDtos.isEmpty()) {
            return shortEventsEnrichment(eventShortDtos);
        }
        return eventShortDtos;
    }

    @Override
    @Transactional
    public EventFullDto createEvent(Integer userId, NewEventDto newEventDto) {
        Optional<User> userOpt = usersRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        Event event = EventMapper.toEvent(newEventDto);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Incorrect event date");
        }
        event.setCategory(categoryRepository.findById(newEventDto.getCategory()).get());
        event.setInitiator(userOpt.get());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventsState.PENDING);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto patchEvent(Integer userId, UpdateEventRequest updateEventRequest, Long eventId) {
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
        if (eventOpt.get().getState().equals(EventsState.PUBLISHED)) {
            throw new ConflictException(String.format("Event with id = %s is published, changes not allowed", eventId));
        }
        Event newEvent = EventMapper.toEvent(updateEventRequest);
        Event event = updateEvent(eventOpt.get(), newEvent);
        if (updateEventRequest.getStateAction() != null) {
            switch (updateEventRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    if (event.getState() != EventsState.CANCELED || event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                        throw new ConflictException("Cannot send event to review");
                    } else {
                        event.setState(EventsState.PENDING);
                    }
                    break;
                case CANCEL_REVIEW:
                    if (event.getState() != EventsState.PENDING) {
                        throw new ConflictException("Cannot cancel the event");
                    } else
                        event.setState(EventsState.CANCELED);
                    break;
                default:
                    throw new ValidationException(String.format("Invalid state action - %s", updateEventRequest.getStateAction()));
            }
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        return fullEventsEnrichment(List.of(eventFullDto)).get(0);
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
        return fullEventsEnrichment(List.of(eventFullDto)).get(0);
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long eventId, Integer userId) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NoObjectException(String.format("Event with id = %s was not found", eventId));
        }
        return RequestMapper.toParticipationRequestDtoList(requestRepository.findByEvent(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsStatus(Long eventId, Integer userId, RequestStatusUpdateDto requestStatusUpdateDto) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NoObjectException(String.format("Event with id = %s was not found", eventId));
        }
        Integer participantLimit = eventRepository.findById(eventId).get().getParticipantLimit();
        List<Request> requestsForConfirmation = new ArrayList<>();
        List<Request> requestsForRejecting = new ArrayList<>();
        List<Request> pendingRequests = requestRepository.findAllById(requestStatusUpdateDto.getRequestIds());
        if (participantLimit == 0) {
            participantLimit = pendingRequests.size();
        }
        if (requestJdbcRepository.getRequestsByStatus(List.of(eventId), RequestStatus.CONFIRMED).get(eventId) != null) {
            participantLimit = Math.toIntExact(participantLimit - requestJdbcRepository.getRequestsByStatus(List.of(eventId), RequestStatus.CONFIRMED).get(eventId));
        }
        if (requestStatusUpdateDto.getStatus() == RequestStatus.CONFIRMED) {
            for (Request request : pendingRequests) {
                if (participantLimit > 0) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    request = requestRepository.save(request);
                    requestsForConfirmation.add(request);
                    participantLimit--;
                } else {
                    throw new ConflictException(String.format("Participant limit of event with id = %s is over", eventId));
                }
            }
        }
        if (requestStatusUpdateDto.getStatus() == RequestStatus.REJECTED) {
            for (Request request : pendingRequests) {
                if (request.getStatus() == RequestStatus.CONFIRMED) {
                    throw new ConflictException("Could not reject confirmed request");
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    request = requestRepository.save(request);
                    requestsForRejecting.add(request);
                }
            }
        }

        return new EventRequestStatusUpdateResult(RequestMapper.toParticipationRequestDtoList(requestsForConfirmation),
                RequestMapper.toParticipationRequestDtoList(requestsForRejecting));
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(Integer[] categories, Integer[] users, String[] states, String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime start;
        LocalDateTime end;
        List<Event> events;
        boolean categoriesExists = false, userExists = false, stateExists = false;
        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, DATE_TIME_FORMATTER);
        }
        if (rangeEnd == null) {
            end = LocalDateTime.of(3000, 1, 1, 0, 0);
        } else {
            end = LocalDateTime.parse(rangeEnd, DATE_TIME_FORMATTER);
        }
        if (categories != null && categories.length > 0) {
            categoriesExists = true;
        }
        if (users != null && users.length > 0) {
            userExists = true;
        }
        if (states != null && states.length > 0) {
            stateExists = true;
        }
        if (categoriesExists && userExists && stateExists) {
            events = eventRepository.getForAdminWithCatsAndUsersAndStates(Arrays.asList(categories), Arrays.asList(users), Arrays.asList(states), start, end, PageRequest.of(from / size, size)).getContent();
        } else if (categoriesExists && userExists) {
            events = eventRepository.getForAdminWithCatsAndUsers(Arrays.asList(categories), Arrays.asList(users), start, end, PageRequest.of(from / size, size)).getContent();
        } else if (categoriesExists && stateExists) {
            events = eventRepository.getForAdminWithCatsAndStates(Arrays.asList(categories), Arrays.asList(states), start, end, PageRequest.of(from / size, size)).getContent();
        } else if (categoriesExists) {
            events = eventRepository.getForAdminWithCats(Arrays.asList(categories), start, end, PageRequest.of(from / size, size)).getContent();
        } else if (userExists && stateExists) {
            events = eventRepository.getForAdminWithUsersAndStates(Arrays.asList(users), Arrays.asList(states), start, end, PageRequest.of(from / size, size)).getContent();
        } else if (stateExists) {
            events = eventRepository.getForAdminWithStates(Arrays.asList(states), start, end, PageRequest.of(from / size, size)).getContent();
        } else if (userExists) {
            events = eventRepository.getForAdminWithUsers(Arrays.asList(users), start, end, PageRequest.of(from / size, size)).getContent();
        } else {
            events = eventRepository.getForAdmin(start, end, PageRequest.of(from / size, size)).getContent();
        }
        List<EventFullDto> eventFullDtos = EventMapper.toEventFullDtoList(events);
        if (!eventFullDtos.isEmpty()) {
            return fullEventsEnrichment(eventFullDtos);
        } else {
            throw new ValidationException("Events must be published");
        }
    }

    @Override
    @Transactional
    public EventFullDto patchEventForAdmin(Long eventId, UpdateEventRequest updateEventRequest) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NoObjectException(String.format("Event with id = %s was not found", eventId));
        }
        Event newEvent = EventMapper.toEvent(updateEventRequest);
        Event event = updateEvent(eventOpt.get(), newEvent);
        if (updateEventRequest.getStateAction() != null) {
            switch (updateEventRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState() != EventsState.PENDING || event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                        throw new ConflictException("Cannot publish the event");
                    } else {
                        event.setState(EventsState.PUBLISHED);
                    }
                    break;
                case REJECT_EVENT:
                    if (event.getState() == EventsState.PUBLISHED) {
                        throw new ConflictException("Cannot reject the event");
                    } else
                        event.setState(EventsState.CANCELED);
            }
        }
        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        return fullEventsEnrichment(List.of(eventFullDto)).get(0);
    }

    private Map<Long, Long> getEventViews(List<Long> events) {
        List<String> formatter = new ArrayList<>();
        for (Long event : events) {
            String format = String.format("/events/%s", event);
            formatter.add(format);
        }
        String[] uris = formatter.toArray(new String[0]);
        ResponseEntity<Object> eventStatEntity = statsClient.getStats(LocalDateTime.of(2020, 1, 1, 0, 0).format(DATE_TIME_FORMATTER),
                LocalDateTime.of(2030, 1, 1, 0, 0).format(DATE_TIME_FORMATTER),
                uris, true);

        List<StatsDto> eventStats = mapper.convertValue(eventStatEntity.getBody(), new TypeReference<>() {
        });
        Map<Long, Long> eventViews = new HashMap<>();
        for (StatsDto eventStat : eventStats) {
            Long eventId = Long.parseLong(eventStat.getUri().replace("/events/", ""));
            eventViews.put(eventId, eventStat.getHits());
        }
        return eventViews;
    }

    private List<EventShortDto> shortEventsEnrichment(List<EventShortDto> eventShortDtos) {
        List<Long> eventsIds = eventShortDtos.stream().map(EventShortDto::getId).collect(Collectors.toList());
        Map<Long, Long> eventViews = getEventViews(eventsIds);
        Map<Long, Long> eventConfirmedRequests = requestJdbcRepository.getRequestsByStatus(eventsIds, RequestStatus.CONFIRMED);
        for (EventShortDto eventShortDto : eventShortDtos) {
            eventShortDto.setViews(eventViews.get(eventShortDto.getId()));
            Long confirmedRequests = eventConfirmedRequests.get(eventShortDto.getId());
            if (confirmedRequests != null) {
                eventShortDto.setConfirmedRequests(eventConfirmedRequests.get(eventShortDto.getId()));
            } else {
                eventShortDto.setConfirmedRequests(0L);
            }
        }
        return eventShortDtos;
    }

    private List<EventFullDto> fullEventsEnrichment(List<EventFullDto> eventFullDtos) {
        List<Long> eventsIds = eventFullDtos.stream().map(EventFullDto::getId).collect(Collectors.toList());
        Map<Long, Long> eventViews = getEventViews(eventsIds);
        Map<Long, Long> eventConfirmedRequests = requestJdbcRepository.getRequestsByStatus(eventsIds, RequestStatus.CONFIRMED);
        List<Comment> comments = commentRepository.findByEventIdInAndState(eventsIds, PUBLISHED.toString());
        Map<Long, List<ShortCommentDto>> commentDtos = new HashMap<>();
        for (EventFullDto eventFullDto : eventFullDtos) {
            eventFullDto.setViews(eventViews.get(eventFullDto.getId()));
            Long confirmedRequests = eventConfirmedRequests.get(eventFullDto.getId());
            if (confirmedRequests != null) {
                eventFullDto.setConfirmedRequests(eventConfirmedRequests.get(eventFullDto.getId()));
            } else {
                eventFullDto.setConfirmedRequests(0L);
            }
            List<ShortCommentDto> commentDtos1 = comments.stream()
                    .filter(comment -> Objects.equals(comment.getEvent().getId(), eventFullDto.getId()))
                    .map(CommentMapper::toShortCommentDto)
                    .collect(Collectors.toList());
            eventFullDto.setComments(new HashSet<>(commentDtos1));
        }
        return eventFullDtos;
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
            if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("EventDate must be after now + 2 hours");
            }
            event.setEventDate(newEvent.getEventDate());
        }
        if (newEvent.getLocationLat() != null && !newEvent.getLocationLat().equals(event.getLocationLat())) {
            event.setLocationLat(newEvent.getLocationLat());
        }
        if (newEvent.getLocationLon() != null && !newEvent.getLocationLon().equals(event.getLocationLon())) {
            event.setLocationLon(newEvent.getLocationLon());
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
