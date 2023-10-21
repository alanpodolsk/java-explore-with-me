package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    public HitDto saveHit(HitDto hitDto);
    public List<StatsDto> getStats(String start, String end, String[] uris, Boolean unique);


}
