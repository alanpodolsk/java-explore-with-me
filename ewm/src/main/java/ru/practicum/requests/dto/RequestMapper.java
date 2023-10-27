package ru.practicum.requests.dto;

import ru.practicum.requests.model.Request;

import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {

    public static Request toRequest(ParticipationRequestDto participationRequestDto) {
        return new Request(
                participationRequestDto.getId(),
                participationRequestDto.getRequester(),
                participationRequestDto.getEvent(),
                participationRequestDto.getCreated(),
                participationRequestDto.getStatus());
    }

    public static ParticipationRequestDto participationRequestDto(Request request){
        return new ParticipationRequestDto(
                request.getId(),
                request.getRequester(),
                request.getEvent(),
                request.getCreated(),
                request.getStatus()
        );
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtoList (List<Request> requests){
        return requests.stream().map(RequestMapper::participationRequestDto).collect(Collectors.toList());
    }
}
