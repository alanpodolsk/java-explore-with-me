package ru.practicum.requests.dto;

import ru.practicum.requests.model.Request;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        return new Request(
                participationRequestDto.getId(),
                participationRequestDto.getEvent(),
                participationRequestDto.getRequester(),
                participationRequestDto.getCreated(),
                participationRequestDto.getStatus());
    }

    public static ParticipationRequestDto participationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getEvent(),
                request.getRequester(),
                request.getCreated(),
                request.getStatus()
        );
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(List<Request> requests) {
        return requests.stream().map(RequestMapper::participationRequestDto).collect(Collectors.toList());
    }
}
