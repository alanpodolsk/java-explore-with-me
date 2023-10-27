package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.events.model.Event;
import ru.practicum.location.model.Location;
import ru.practicum.users.dto.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class EventMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event toEvent(NewEventDto newEventDto) {

        return new Event(
                null,
                newEventDto.getAnnotation(),
                null,
                null,
                newEventDto.getDescription(),
                LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER),
                null,
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon(),
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.getRequestModeration(),
                null,
                newEventDto.getTitle()
        );
    }

    public static Event toEvent(UpdateEventRequest updateEventRequest) {
        LocalDateTime eventTime;
        if (updateEventRequest.getEventDate() == null){
            eventTime = null;
        } else {
            eventTime = LocalDateTime.parse(updateEventRequest.getEventDate(), DATE_TIME_FORMATTER);
        }
        return new Event(
                null,
                updateEventRequest.getAnnotation(),
                null,
                null,
                updateEventRequest.getDescription(),
                eventTime,
                null,
                updateEventRequest.getLocation().getLat(),
                updateEventRequest.getLocation().getLon(),
                updateEventRequest.getPaid(),
                updateEventRequest.getParticipantLimit(),
                null,
                updateEventRequest.getRequestModeration(),
                null,
                updateEventRequest.getTitle()
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                UserMapper.toUserShortDto(event.getInitiator()),
                new Location(event.getLocationLat(), event.getLocationLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                null,
                null
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getEventDate(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                null,
                null
        );
    }

    public static List<EventShortDto> toEventShortDtoList(List<Event> events) {
        return events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }
}
