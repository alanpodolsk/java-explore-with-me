package ru.practicum.service;

import ru.practicum.HitDto;
import ru.practicum.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    public HitDto saveHit(HitDto hitDto);
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique);


}
