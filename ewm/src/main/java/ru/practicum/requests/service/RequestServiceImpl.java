package ru.practicum.requests.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestMapper;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.users.repository.UsersRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UsersRepository usersRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Integer userId) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        return RequestMapper.toParticipationRequestDtoList(requestRepository.findByRequester(userId));
    }

    @Override
    public ParticipationRequestDto createRequest(Integer userId, ParticipationRequestDto participationRequestDto) {
        if (!usersRepository.existsById(userId)) {
            throw new NoObjectException(String.format("User with id = %s was not found", userId));
        }
        Request request = RequestMapper.toRequest(participationRequestDto);
        if (request.getRequester() == null) {
            request.setRequester(userId);
        }
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
        request.setStatus(RequestStatus.REJECTED);
        return RequestMapper.participationRequestDto(requestRepository.save(request));
    }
}
