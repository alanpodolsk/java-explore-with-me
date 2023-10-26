package ru.practicum.events.dto;

import lombok.*;
import ru.practicum.location.model.Location;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    String annotation;
    Integer category;
    String description;
    String eventDate;
    Location location;
    Integer participantLimit;
    Boolean paid;
    String title;
    Boolean requestModeration;
}
