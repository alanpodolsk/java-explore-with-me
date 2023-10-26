package ru.practicum.requests.dto;

import ru.practicum.requests.model.Request;

import java.time.LocalDateTime;

public class RequestMapper {
    public static Request toRequest(ParticipationRequestDto participationRequestDto){
        return new Request(
                participationRequestDto.getId(),
                participationRequestDto.getRequester(),
                participationRequestDto.getEvent(),
                LocalDateTime.parse(participationRequestDto.getCreated()
        )
    }
}
