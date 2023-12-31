package ru.practicum.events.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class EventsPubController {

    private final StatsClient statsClient;
    private final EventService eventService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        postHit("/events/" + id, request.getRemoteAddr());
        return eventService.getEventById(id);
    }

    @GetMapping
    public List<EventShortDto> getEvents(HttpServletRequest request, @RequestParam(defaultValue = "") String text, @RequestParam (required = false) Integer[] categories,
                                         @RequestParam(required = false) Boolean paid, @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd, @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(defaultValue = "") String sort,
                                         @Validated @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @Validated @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        postHit("/events", request.getRemoteAddr());
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    private void postHit(String uri, String ip) {
        HitDto hitDto = new HitDto(
                "ewm-main-service",
                uri,
                ip,
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
        statsClient.postHit(hitDto);
    }
}
