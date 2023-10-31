package ru.practicum.requests.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.requests.dto.RequestsCountDto;
import ru.practicum.requests.model.RequestStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class RequestJdbcRepositoryImpl implements RequestJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, Long> getRequestsByStatus(List<Long> events, RequestStatus status) {
        Map<Long, Long> confirmedRequests = new HashMap<>();
        List<RequestsCountDto> requestsCountDtos = jdbcTemplate.query("SELECT event, count(id) AS count FROM requests WHERE event in (" + getEventsForSQL(events) + ") and status = ? GROUP BY event", requestsCountDtoRowMapper(), status.toString());
        for (RequestsCountDto requestsCountDto : requestsCountDtos) {
            confirmedRequests.put(requestsCountDto.getEvent(), requestsCountDto.getCount());
        }
        return confirmedRequests;
    }

    private RowMapper<RequestsCountDto> requestsCountDtoRowMapper() {
        return (rs, rowNum) -> {
            RequestsCountDto requestsCountDto = new RequestsCountDto();
            requestsCountDto.setEvent(rs.getLong("event"));
            requestsCountDto.setCount(rs.getLong("count"));
            return requestsCountDto;
        };
    }

    private String getEventsForSQL(List<Long> events) {
        StringBuilder sb = new StringBuilder();
        for (Long event : events) {
            sb.append(event).append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
