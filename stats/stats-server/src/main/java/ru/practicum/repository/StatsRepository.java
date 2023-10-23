package ru.practicum.repository;

import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {
    List<StatsDto> getUniqueStats(LocalDateTime start, LocalDateTime end, String uri);

    List<StatsDto> getNonUniqueStats(LocalDateTime start, LocalDateTime end, String uri);

    List<StatsDto> getUniqueStatsNullUri(LocalDateTime start, LocalDateTime end);

    List<StatsDto> getNonUniqueStatsNullUri(LocalDateTime start, LocalDateTime end);
}
