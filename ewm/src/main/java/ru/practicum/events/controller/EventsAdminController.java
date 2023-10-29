package ru.practicum.events.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventRequest;
import ru.practicum.events.service.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
public class EventsAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsForAdmin(@RequestParam (required = false) Integer[] categories, @RequestParam (required = false) Integer[] users, @RequestParam (required = false) String[] states,
                                                @RequestParam(required = false) String rangeStart, @RequestParam(required = false) String rangeEnd,
                                                @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsForAdmin(categories, users, states, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(("/{eventId}"))
    public EventFullDto patchEventForAdmin(@PathVariable Long eventId, @Validated @RequestBody UpdateEventRequest updateEventRequest) {
        return eventService.patchEventForAdmin(eventId, updateEventRequest);
    }

}
