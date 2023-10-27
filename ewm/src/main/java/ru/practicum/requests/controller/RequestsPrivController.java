package ru.practicum.requests.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@AllArgsConstructor
public class RequestsPrivController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Integer userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping
    public ParticipationRequestDto createRequest(@PathVariable Integer userId, @RequestBody ParticipationRequestDto participationRequestDto) {
        return requestService.createRequest(userId, participationRequestDto);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByUser(@PathVariable Integer userId, @PathVariable Long requestId) {
        return requestService.cancelRequestByUser(userId, requestId);
    }
}
