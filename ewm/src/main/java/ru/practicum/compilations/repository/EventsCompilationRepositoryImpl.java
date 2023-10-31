package ru.practicum.compilations.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventsCompilationRepositoryImpl implements EventsCompilationRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEventToCompilation(Long eventId, Integer compId) {
        String sqlSubQuery = "INSERT INTO events_compilations (comp_id, event_id) VALUES (?,?)";
        jdbcTemplate.update(sqlSubQuery, compId, eventId);
    }

    @Override
    public void deleteRowsByEventId(Long eventId) {
        String sqlSubQuery = "DELETE FROM events_compilations WHERE event_id = ?";
        jdbcTemplate.update(sqlSubQuery, eventId);
    }

    @Override
    public void deleteRowsByCompId(Integer compId) {
        String sqlSubQuery = "DELETE FROM events_compilations WHERE comp_id = ?";
        jdbcTemplate.update(sqlSubQuery, compId);
    }
}
