package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.EventStateAction;
import ru.practicum.location.model.Location;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    Integer category;
    @Size(min = 20, max = 7000)
    String description;
    String eventDate;
    Location location;
    Integer participantLimit;
    Boolean paid;
    @Size(min = 3, max = 120)
    String title;
    Boolean requestModeration;
    EventStateAction stateAction;
}
