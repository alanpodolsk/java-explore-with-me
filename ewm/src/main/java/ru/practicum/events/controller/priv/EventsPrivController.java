package ru.practicum.events.controller.priv;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@AllArgsConstructor
public class EventsPrivController {
    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Integer userId){
        return null;
    }

    @PostMapping(("users/{userId}/events"))
    public EventFullDto createEvent(@PathVariable Integer userId, @RequestBody NewEventDto newEventDto){
        return null;
    }

    @GetMapping(("users/{userId}/events/{eventId}"))
    public EventFullDto getEventCreatedByUser(@PathVariable Integer userId, @PathVariable Integer eventId){
        return null;
    }

    @PatchMapping(("users/{userId}/events/{eventId}"))
    public EventFullDto patchEvent(@PathVariable Integer userId, @PathVariable Integer eventId, @RequestBody NewEventDto newEventDto){
        return null;
    }
}
