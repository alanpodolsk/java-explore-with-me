package ru.practicum.requests.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;

import java.util.Map;
import java.util.List;


public interface RequestRepository extends JpaRepository<Request,Long> {
    List<Request> findByEvent(Long eventId);

    List<Request> findByEventAndStatus(Long eventId, RequestStatus status, Sort sort);

    List<Request> findByRequester(Integer userId);

    @Query (value = "SELECT event, count(id) FROM requests WHERE event in (&1) and status = ?2", nativeQuery = true)
    Map<Long, Long> findCountByEventInAndStatus(List<Long> events, RequestStatus status);
    @Query (value = "UPDATE requests SET status = ?2 WHERE id in (?1)", nativeQuery = true)
    void updateRequestStatus(List<Long> requests, RequestStatus status);
}
