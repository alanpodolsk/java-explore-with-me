package ru.practicum.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventsState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> getByInitiatorId(Integer userId);

    Optional<Event> getByIdAndState(Long id, EventsState state);

    @Query(value = "SELECT " +
            "e.* " +
            "FROM " +
            "events e" +
            "WHERE e.status = 'PUBLISHED'" +
            "AND (annotation ilike '%?1%' OR description ilike '%?1%')" +
            "AND category_id in(?2)" +
            "AND paid in (?3)" +
            "AND eventDate BETWEEN ?4 AND ?5" +
            "AND (SELECT (COUNT (id) FROM requests where event_id = e.id)) < e.participant_limit", nativeQuery = true)
    Page<Event> getForPublicWithCategoriesAndLimit(String text, Integer[] categories, Boolean[] paid, String start, String end, Pageable pageable);

    @Query(value = "SELECT " +
            "e.* " +
            "FROM " +
            "events e" +
            "WHERE e.status = 'PUBLISHED'" +
            "AND (annotation ilike '%?1%' OR description ilike '%?1%')" +
            "AND category_id in(?2)" +
            "AND paid in (?3)" +
            "AND eventDate BETWEEN ?4 AND ?5", nativeQuery = true)
    Page<Event> getForPublicWithCategories(String text, Integer[] categories, Boolean[] paid, String start, String end, Pageable pageable);

    @Query(value = "SELECT " +
            "e.* " +
            "FROM " +
            "events e" +
            "WHERE e.status = 'PUBLISHED'" +
            "AND (annotation ilike '%?1%' OR description ilike '%?1%')" +
            "AND paid in (?2)" +
            "AND eventDate BETWEEN ?3 AND ?4" +
            "AND (SELECT (COUNT (id) FROM requests where event_id = e.id)) < e.participant_limit", nativeQuery = true)
    Page<Event> getForPublicWithLimit(String text, Boolean[] paid, String start, String end, Pageable pageable);

    @Query(value = "SELECT " +
            "e.* " +
            "FROM " +
            "events e" +
            "WHERE e.status = 'PUBLISHED'" +
            "AND (annotation ilike '%?1%' OR description ilike '%?1%')" +
            "AND paid in (?2)" +
            "AND eventDate BETWEEN ?3 AND ?4", nativeQuery = true)
    Page<Event> getForPublic(String text, Boolean[] paid, String start, String end, Pageable pageable);
}
