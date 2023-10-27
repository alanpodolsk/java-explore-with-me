package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requests.model.Request;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request,Long> {
    List<Request> findByEvent(Long eventId);
}
