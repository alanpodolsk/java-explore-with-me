package ru.practicum.requests.service;

import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Integer userId);

    ParticipationRequestDto createRequest(Integer userId, Long eventId);

    ParticipationRequestDto cancelRequestByUser(Integer userId, Long requestId);

}