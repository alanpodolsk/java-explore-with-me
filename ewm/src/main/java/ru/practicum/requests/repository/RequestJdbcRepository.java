package ru.practicum.requests.repository;

import ru.practicum.requests.dto.RequestsCountDto;
import ru.practicum.requests.model.RequestStatus;

import java.util.List;
import java.util.Map;

public interface RequestJdbcRepository {
    Map<Long,Long> getRequestsByStatus(List<Long> events, RequestStatus status);
}
