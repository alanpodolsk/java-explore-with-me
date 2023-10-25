package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewEventDto that = (NewEventDto) o;
        return Objects.equals(annotation, that.annotation) && Objects.equals(category, that.category) && Objects.equals(eventDate, that.eventDate) && Objects.equals(initiator, that.initiator) && Objects.equals(paid, that.paid) && Objects.equals(title, that.title) && Objects.equals(confirmedRequests, that.confirmedRequests) && Objects.equals(views, that.views);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, category, eventDate, initiator, paid, title, confirmedRequests, views);
    }
}
