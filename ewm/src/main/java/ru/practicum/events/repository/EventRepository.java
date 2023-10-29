package ru.practicum.events.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventsState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> getByInitiatorId(Integer userId, Pageable pageable);

    Optional<Event> getByIdAndState(Long id, EventsState state);

    @Query(value = "SELECT " +
            "e.* " +
            "FROM " +
            "events e " +
            "WHERE state = 'PUBLISHED' " +
            "AND (annotation ilike ?1 OR description ilike ?1) " +
            "AND category_id in (?2) " +
            "AND paid in (?3) " +
            "AND event_date BETWEEN ?4 AND ?5 " +
            "AND (SELECT (COUNT (id) FROM requests where event_id = e.id)) < e.participant_limit", nativeQuery = true)
    Page<Event> getForPublicWithCategoriesAndLimit(String text, List<Integer> categories, List<Boolean> paid, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE state = 'PUBLISHED' " +
            "AND (annotation ilike ?1 OR description ilike ?1) " +
            "AND category_id in(?2) " +
            "AND paid in (?3) " +
            "AND event_date BETWEEN ?4 AND ?5", nativeQuery = true)
    Page<Event> getForPublicWithCategories(String text, List<Integer> categories, List<Boolean> paid, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "e.* " +
            "FROM " +
            "events e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (annotation ilike ?1 OR description ilike ?1) " +
            "AND paid in (?2) " +
            "AND event_date BETWEEN ?3 AND ?4 " +
            "AND (SELECT (COUNT (id) FROM requests where event_id = e.id)) < e.participant_limit", nativeQuery = true)
    Page<Event> getForPublicWithLimit(String text, List<Boolean> paid, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE category_id in (?1) " +
            "AND initiator_id in (?2) " +
            "AND state in (?3) " +
            "AND event_date BETWEEN ?4 AND ?5 ", nativeQuery = true)
    Page<Event> getForAdminWithCatsAndUsersAndStates(List<Integer> categories, List<Integer> users, List<String> states, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE event_date BETWEEN ?1 AND ?2 ", nativeQuery = true)
    Page<Event> getForAdmin(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE category_id in (?1) " +
            "AND initiator_id in (?2) " +
            "AND event_date BETWEEN ?3 AND ?4 ", nativeQuery = true)
    Page<Event> getForAdminWithCatsAndUsers(List<Integer> categories, List<Integer> users, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE category_id in (?1) " +
            "AND state in (?2) " +
            "AND event_date BETWEEN ?3 AND ?4 ", nativeQuery = true)
    Page<Event> getForAdminWithCatsAndStates(List<Integer> categories, List<String> states, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE category_id in (?1) " +
            "AND event_date BETWEEN ?2 AND ?3 ", nativeQuery = true)
    Page<Event> getForAdminWithCats(List<Integer> categories, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE initiator_id in (?1) " +
            "AND event_date BETWEEN ?2 AND ?3 ", nativeQuery = true)
    Page<Event> getForAdminWithUsers(List<Integer> users, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE state in (?1) " +
            "AND event_date BETWEEN ?2 AND ?3 ", nativeQuery = true)
    Page<Event> getForAdminWithStates(List<String> states, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "* " +
            "FROM " +
            "events " +
            "WHERE initiator_id in (?1) " +
            "AND state in (?2) " +
            "AND event_date BETWEEN ?3 AND ?4 ", nativeQuery = true)
    Page<Event> getForAdminWithUsersAndStates(List<Integer> users, List<String> states, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT " +
            "e.* " +
            "FROM " +
            "events e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (annotation ilike ?1 OR description ilike ?1) " +
            "AND paid in (?2) " +
            "AND event_date BETWEEN ?3 AND ?4", nativeQuery = true)
    Page<Event> getForPublic(String text, List<Boolean> paid, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query(value = "SELECT e.* from events e join events_compilations ec on e.id = ec.event_id WHERE ec.comp_id = ?1", nativeQuery = true)
    List<Event> findAllEventsInCompilation(Integer compId);
}
