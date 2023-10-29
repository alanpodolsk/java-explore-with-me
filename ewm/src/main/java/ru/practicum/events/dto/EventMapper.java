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
        Double lat = null;
        Double lon = null;
        LocalDateTime eventTime = null;
        Boolean paid = false;
        Integer participantLimit = 0;
        Boolean requestModeration = true;
        if (newEventDto.getLocation() != null) {
            lat = newEventDto.getLocation().getLat();
            lon = newEventDto.getLocation().getLon();
        }
        if (newEventDto.getEventDate() != null) {
            eventTime = LocalDateTime.parse(newEventDto.getEventDate(), DATE_TIME_FORMATTER);
        }
        if(newEventDto.getPaid() != null){
            paid = newEventDto.getPaid();
        }
        if(newEventDto.getRequestModeration() != null){
            paid = newEventDto.getRequestModeration();
        }
        if(newEventDto.getParticipantLimit() != null){
            participantLimit = newEventDto.getParticipantLimit();
        }

        return new Event(
                null,
                newEventDto.getAnnotation(),
                null,
                null,
                newEventDto.getDescription(),
                eventTime,
                null,
                lat,
                lon,
                paid,
                participantLimit,
                null,
                requestModeration,
                null,
                newEventDto.getTitle()
        );
    }

    public static Event toEvent(UpdateEventRequest updateEventRequest) {
        Double lat = null;
        Double lon = null;
        LocalDateTime eventTime = null;
        if (updateEventRequest.getLocation() != null) {
            lat = updateEventRequest.getLocation().getLat();
            lon = updateEventRequest.getLocation().getLon();
        }
        if (updateEventRequest.getEventDate() != null) {
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
                lat,
                lon,
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
                event.getEventDate().format(DATE_TIME_FORMATTER),
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
