package ru.practicum.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.EventsState;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> getByInitiatorId(Integer userId);

    Optional<Event> getByIdAndState(Long id, EventsState state);
}
