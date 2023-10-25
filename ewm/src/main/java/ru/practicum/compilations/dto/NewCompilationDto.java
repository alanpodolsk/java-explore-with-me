package ru.practicum.compilations.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    @NotNull
    Boolean pinned;
    @NotNull
    String title;
    List<Long> events;
}
