package ru.practicum.requests.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.requests.dto.RequestsCountDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.model.RequestStatus;

import java.util.Map;
import java.util.List;
import java.util.Optional;


public interface RequestRepository extends JpaRepository<Request,Long> {
    List<Request> findByEvent(Long eventId);
    List<Long> findIdByEvent(Long eventId);

    List<Request> findByEventAndStatus(Long eventId, RequestStatus status, Sort sort);

    List<Request> findByRequester(Integer userId);
}
