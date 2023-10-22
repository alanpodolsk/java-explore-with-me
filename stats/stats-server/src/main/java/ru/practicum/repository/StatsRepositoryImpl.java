package ru.practicum.repository;

import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Component
public class StatsRepositoryImpl implements StatsRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<StatsDto> getUniqueStats(LocalDateTime start, LocalDateTime end, String[] uri) {
        return jdbcTemplate.query("SELECT app, uri, count(distinct ip) as hits FROM hits WHERE timestamp between ? AND ? AND uri in (?) GROUP BY app, uri", statsDtoRowMapper(), start, end, uri);
    }

    public List<StatsDto> getNonUniqueStats(LocalDateTime start, LocalDateTime end, String[] uri) {
        return jdbcTemplate.query("SELECT app, uri, count(id) as hits FROM hits WHERE timestamp between ? AND ? AND uri in (?) GROUP BY app, uri", statsDtoRowMapper(), start, end, uri);
    }

    @Override
    public List<StatsDto> getUniqueStatsNullUri(LocalDateTime start, LocalDateTime end) {
        return jdbcTemplate.query("SELECT app, uri, count(distinct ip) as hits FROM hits WHERE timestamp between ? AND ? GROUP BY app, uri", statsDtoRowMapper(), start, end);
    }

    @Override
    public List<StatsDto> getNonUniqueStatsNullUri(LocalDateTime start, LocalDateTime end) {
        return jdbcTemplate.query("SELECT app, uri, count(id) as hits FROM hits WHERE timestamp between ? AND ? GROUP BY app, uri", statsDtoRowMapper(), start, end);
    }

    private RowMapper<StatsDto> statsDtoRowMapper() {
        return (rs, rowNum) -> {
            StatsDto statsDto = new StatsDto();
            statsDto.setApp(rs.getString("app"));
            statsDto.setUri(rs.getString("uri"));
            statsDto.setHits(rs.getInt("hits"));
            return statsDto;
        };
    }
}
