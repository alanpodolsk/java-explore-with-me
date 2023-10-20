package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.HitDto;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public HitDto saveHit(@RequestBody HitDto hitDto){
        return statsService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end, @RequestParam String[] uris, @RequestParam Boolean unique){
        return statsService.getStats(start, end, uris, unique);
    }
}
