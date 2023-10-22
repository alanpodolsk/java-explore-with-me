package ru.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitMapper;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;
import ru.practicum.repository.HitsRepository;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@AllArgsConstructor
@Component
public class StatsServiceImpl implements StatsService {

    private final HitsRepository hitsRepository;
    private final StatsRepository statsRepository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public HitDto saveHit(HitDto hitDto) {
        if (hitDto != null) {
            return HitMapper.toHitDto(hitsRepository.save(HitMapper.toHit(hitDto)));
        } else {
            throw new NoObjectException("Передан пустой Hit");
        }
    }

    @Override
    public List<StatsDto> getStats(String start, String end, String[] uris, Boolean unique) {
        if (start == null || end == null) {
            throw new NoObjectException("Переданы не все параметры, необходимые для исполнения запроса");
        }
        LocalDateTime startDate = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Дата окончания должна быть позже даты начала периода");
        }
        List<StatsDto> statsResponse = new ArrayList<>();
        if (uris.length == 0) {
            if (unique) {
                statsResponse.addAll(statsRepository.getUniqueStatsNullUri(startDate, endDate));
            } else {
                statsResponse.addAll(statsRepository.getNonUniqueStatsNullUri(startDate, endDate));
            }
        } else {
            for (String uri : uris) {
                if (unique) {
                    statsResponse.addAll(statsRepository.getUniqueStats(startDate, endDate, uri + '%'));
                } else {
                    statsResponse.addAll(statsRepository.getNonUniqueStats(startDate, endDate, uri + '%'));
                }
            }
        }
        Set<StatsDto> setStatsResponse = new HashSet<>(statsResponse);
        statsResponse.clear();
        statsResponse.addAll(setStatsResponse);
        return statsResponse;
    }
}
