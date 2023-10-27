package ru.practicum.events.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.service.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
public class EventsAdminController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsForAdmin(@RequestParam Integer[] categories, @RequestParam Integer[] users, @RequestParam String[] states,
                                                @RequestParam(defaultValue = "") String rangeStart, @RequestParam(defaultValue = "") String rangeEnd,
                                                @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsForAdmin(categories, users, states, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(("/{eventId}"))
    public EventFullDto patchEventForAdmin(@PathVariable Long eventId, @RequestBody NewEventDto newEventDto) {
        return eventService.patchEventForAdmin(eventId, newEventDto);
    }

}
