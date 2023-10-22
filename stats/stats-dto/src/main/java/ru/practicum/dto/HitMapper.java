package ru.practicum.dto;

import ru.practicum.model.Hit;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class HitMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static HitDto toHitDto(Hit hit) {
        return new HitDto(
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp().format(DATE_TIME_FORMATTER)
        );
    }

    public static Hit toHit(HitDto hitDto) {
        return new Hit(
                null,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                LocalDateTime.parse(hitDto.getTimestamp(), DATE_TIME_FORMATTER)
        );
    }
}
