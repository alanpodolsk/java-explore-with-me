package ru.practicum.requests.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventsState;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestJdbcRepository;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.repository.UsersRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Component
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final RequestJdbcRepository requestJdbcRepository;
    private final UsersRepository usersRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Integer userId) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }

        return RequestMapper.toParticipationRequestDtoList(requestRepository.findByRequester(userId));
    }

    @Override
    public ParticipationRequestDto createRequest(Integer userId, Long eventId) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            throw new NoObjectException(String.format("Event with id = %s was not found", eventId));
        }
        if (eventOpt.get().getState() != EventsState.PUBLISHED) {
            throw new ConflictException(String.format("Event with id = %s was not published", eventId));
        }
        if (Objects.equals(eventOpt.get().getInitiator().getId(), userId)) {
            throw new ConflictException(String.format("User with id = %s could not make request for own event", eventId));
        }
        long actualRequests = requestRepository.findByEvent(eventId).size();
        if(actualRequests >= eventOpt.get().getParticipantLimit()){
            throw new ConflictException(String.format("Participant limit of event with id = %s is over", eventId));
        }
        Request request = new Request(
                null,
                eventId,
                userId,
                LocalDateTime.now(),
                RequestStatus.PENDING
        );

        return RequestMapper.participationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequestByUser(Integer userId, Long requestId) {
        Optional<Request> requestOpt = requestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new NoObjectException(String.format("Request with id = %s was not found", requestId));
        }
        Request request = requestOpt.get();
        if (request.getRequester() == null || !request.getRequester().equals(userId)) {
            throw new ValidationException(String.format("Patch requested by incorrect user with id = %s", userId));
        }
        if(request.getStatus() == RequestStatus.CONFIRMED){
            throw new ConflictException("You can't reject confirmed request");
        }
        request.setStatus(RequestStatus.REJECTED);
        return RequestMapper.participationRequestDto(requestRepository.save(request));
    }
}
