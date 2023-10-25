package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    Long id;
    String annotation;
    CategoryDto category;
    LocalDateTime eventDate;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long confirmedRequests;
    Long views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventShortDto that = (EventShortDto) o;
        return Objects.equals(annotation, that.annotation) && Objects.equals(category, that.category) && Objects.equals(eventDate, that.eventDate) && Objects.equals(initiator, that.initiator) && Objects.equals(paid, that.paid) && Objects.equals(title, that.title) && Objects.equals(confirmedRequests, that.confirmedRequests) && Objects.equals(views, that.views);
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotation, category, eventDate, initiator, paid, title, confirmedRequests, views);
    }
}
