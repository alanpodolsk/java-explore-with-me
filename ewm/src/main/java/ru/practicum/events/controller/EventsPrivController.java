package ru.practicum.events.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.dto.UpdateEventRequest;
import ru.practicum.events.service.EventService;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestStatusUpdateDto;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@AllArgsConstructor
public class EventsPrivController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Integer userId,
                                               @Validated @RequestParam(defaultValue = "0") @Min(0) Integer from, @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Integer userId, @Validated @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping(("/{eventId}"))
    public EventFullDto getEventCreatedByUser(@PathVariable Integer userId, @PathVariable Long eventId) {
        return eventService.getEventForCreator(userId, eventId);
    }

    @PatchMapping(("/{eventId}"))
    public EventFullDto patchEvent(@PathVariable Integer userId, @PathVariable Long eventId, @Validated @RequestBody UpdateEventRequest updateEventRequest) {
        return eventService.patchEvent(userId, updateEventRequest, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long eventId, @PathVariable Integer userId) {
        return eventService.getEventRequests(eventId, userId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequestsStatus(@PathVariable Long eventId, @PathVariable Integer userId,
                                                                    @Validated @RequestBody RequestStatusUpdateDto requestStatusUpdateDto) {
        return eventService.updateEventRequestsStatus(eventId, userId, requestStatusUpdateDto);
    }

}
