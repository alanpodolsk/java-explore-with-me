package ru.practicum.events.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.practicum.location.model.Location;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank
    @Length(min = 20, max = 2000)
    String annotation;
    Integer category;
    @NotBlank
    @Length(min = 20, max = 7000)
    String description;
    String eventDate;
    Location location;
    Integer participantLimit;
    Boolean paid;
    @NotBlank
    @Length(min = 3, max = 120)
    String title;
    Boolean requestModeration;
}
