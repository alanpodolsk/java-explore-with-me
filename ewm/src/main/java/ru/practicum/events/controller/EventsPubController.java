package ru.practicum.events.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
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
        postHit("/events/"+id,request.getRemoteAddr());
        return eventService.getEventById(id);
    }

    @GetMapping
    public List<EventShortDto> getEvents(HttpServletRequest request, @RequestParam (defaultValue = "") String text, @RequestParam Integer[] categories,
                                         @RequestParam (defaultValue = "false") Boolean paid, @RequestParam (defaultValue = "") String rangeStart,
                                         @RequestParam (defaultValue = "") String rangeEnd, @RequestParam (defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam (defaultValue = "") String sort, @RequestParam (defaultValue = "0") Integer from,
                                         @RequestParam (defaultValue = "10") Integer size) {
        postHit("/events",request.getRemoteAddr());
        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    private void postHit(String uri, String ip){
        HitDto hitDto = new HitDto(
                "ewm-main-service",
                uri,
                ip,
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
        statsClient.postHit(hitDto);
    }
}
