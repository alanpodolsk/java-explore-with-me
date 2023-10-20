package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository <StatsDto, Integer> {
    @Query(value = "SELECT app, uri, count(id) as hits FROM hits WHERE timestamp between ?1 AND ?2 AND uri in (?3) GROUP BY app, uri", nativeQuery = true)
    List<StatsDto> getNonUniqueStats(LocalDateTime start, LocalDateTime end, String[] uris);

    @Query(value = "SELECT app, uri, count(distinct ip) as hits FROM hits WHERE timestamp between ?1 AND ?2 AND uri in (?3) GROUP BY app, uri", nativeQuery = true)
    List<StatsDto> getUniqueStats(LocalDateTime start, LocalDateTime end, String[] uris);

}
