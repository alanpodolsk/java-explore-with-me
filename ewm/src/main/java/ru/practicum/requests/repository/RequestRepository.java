package ru.practicum.requests.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByEvent(Long eventId);

    List<Request> findByEventAndStatus(Long eventId, RequestStatus status, Sort sort);

    List<Request> findByRequester(Integer userId);
}
