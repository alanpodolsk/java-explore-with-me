package ru.practicum.events.model;

import lombok.*;
import ru.practicum.categories.model.Category;
import ru.practicum.location.model.Location;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    Long id;
    String annotation;
    Category category;
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    User initiator;
    Double location_lat;
    Double location_lon;
    Boolean paid;
    Integer participantLimit = 0;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventsState state;
    String title;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(annotation, event.annotation) && Objects.equals(category, event.category) && Objects.equals(createdOn, event.createdOn) && Objects.equals(description, event.description) && Objects.equals(eventDate, event.eventDate) && Objects.equals(initiator, event.initiator) && Objects.equals(location_lat, event.location_lat) && Objects.equals(location_lon, event.location_lon) && Objects.equals(paid, event.paid) && Objects.equals(participantLimit, event.participantLimit) && Objects.equals(publishedOn, event.publishedOn) && Objects.equals(requestModeration, event.requestModeration) && state == event.state && Objects.equals(title, event.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, category, createdOn, description, eventDate, initiator, location_lat, location_lon, paid, participantLimit, publishedOn, requestModeration, state, title);
    }
}
