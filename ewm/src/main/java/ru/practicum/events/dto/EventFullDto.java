package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.comments.dto.ShortCommentDto;
import ru.practicum.events.model.EventsState;
import ru.practicum.location.model.Location;
import ru.practicum.users.dto.UserShortDto;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto extends EventShortDto {
    String createdOn;
    String description;
    Location location;
    Integer participantLimit = 0;
    String publishedOn;
    Boolean requestModeration;
    Set<ShortCommentDto> comments;

    public EventFullDto(Long id, String annotation, CategoryDto category, String eventDate, UserShortDto initiator, Boolean paid, String title, Long confirmedRequests, Long views, String createdOn, String description, Location location, Integer participantLimit, String publishedOn, Boolean requestModeration, EventsState state, Set<ShortCommentDto> comments) {
        super(id, annotation, category, eventDate, initiator, paid, title, confirmedRequests, views);
        this.createdOn = createdOn;
        this.description = description;
        this.location = location;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EventFullDto that = (EventFullDto) o;
        return Objects.equals(createdOn, that.createdOn) && Objects.equals(description, that.description) && Objects.equals(location, that.location) && Objects.equals(participantLimit, that.participantLimit) && Objects.equals(publishedOn, that.publishedOn) && Objects.equals(requestModeration, that.requestModeration) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdOn, description, location, participantLimit, publishedOn, requestModeration, state);
    }

    EventsState state;
}
