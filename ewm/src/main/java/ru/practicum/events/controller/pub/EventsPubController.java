package ru.practicum.events.controller.pub;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.dto.HitDto;
import ru.practicum.events.dto.EventFullDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class EventsPubController {

    private final StatsClient statsClient;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Integer id, HttpServletRequest request) {
        HitDto hitDto = new HitDto(
                "ewm-main-service",
                "/events/" + id,
                request.getRemoteAddr(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
        statsClient.postHit(hitDto);
        return null;
    }

    @GetMapping
    public EventFullDto getEvents(HttpServletRequest request, @RequestParam (defaultValue = "") String text, @RequestParam Integer[] categories,
                                  @RequestParam (defaultValue = "false") Boolean paid, @RequestParam (defaultValue = "") String rangeStart,
                                  @RequestParam (defaultValue = "") String rangeEnd, @RequestParam (defaultValue = "false") Boolean onlyAvailable,
                                  @RequestParam (defaultValue = "") String sort, @RequestParam (defaultValue = "0") Integer from,
                                  @RequestParam (defaultValue = "10") Integer size) {
        HitDto hitDto = new HitDto(
                "ewm-main-service",
                "/events",
                request.getRemoteAddr(),
                LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
        statsClient.postHit(hitDto);
        return null;
    }
}
