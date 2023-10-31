package ru.practicum.compilations.repository;

public interface EventsCompilationRepository {
    void addEventToCompilation(Long eventId, Integer compId);

    void deleteRowsByEventId(Long eventId);

    void deleteRowsByCompId(Integer compId);
}
