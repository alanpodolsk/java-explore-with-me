package ru.practicum.events.dto;

import lombok.*;
import ru.practicum.events.model.EventsState;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Integer participantLimit = 0;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventsState state;
    String title;
    Long confirmedRequests;
    Long views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventFullDto that = (EventFullDto) o;
        return Objects.equals(annotation, that.annotation) && Objects.equals(category, that.category) && Objects.equals(createdOn, that.createdOn) && Objects.equals(description, that.description) && Objects.equals(eventDate, that.eventDate) && Objects.equals(initiator, that.initiator) && Objects.equals(paid, that.paid) && Objects.equals(participantLimit, that.participantLimit) && Objects.equals(publishedOn, that.publishedOn) && Objects.equals(requestModeration, that.requestModeration) && state == that.state && Objects.equals(title, that.title) && Objects.equals(confirmedRequests, that.confirmedRequests) && Objects.equals(views, that.views);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, category, createdOn, description, eventDate, initiator, paid, participantLimit, publishedOn, requestModeration, state, title, confirmedRequests, views);
    }
}
