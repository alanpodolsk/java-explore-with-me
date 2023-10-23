package ru.practicum.compilations.dto;

import lombok.*;
import ru.practicum.events.dto.EventShortDto;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    Boolean pinned;
    String title;
    List<EventShortDto> events;
}
