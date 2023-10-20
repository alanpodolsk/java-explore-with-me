package ru.practicum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HitDto {
    String app;
    String uri;
    String ip;
    LocalDateTime timestamp;
}
