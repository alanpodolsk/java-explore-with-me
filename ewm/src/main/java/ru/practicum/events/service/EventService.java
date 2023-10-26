package ru.practicum.events.service;

import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.dto.RequestStatusUpdateDto;

import java.util.List;

public interface EventService {
    EventFullDto getEventById(Long id);

    List<EventShortDto> getEvents(String text, Integer[] categories, Boolean paid, String rangeStart, String rangeEnd,
                                  Boolean onlyAvailable, String sort, Integer from, Integer size);

    List<EventShortDto> getEventsByUser(Integer userId);

    EventFullDto createEvent(Integer userId, NewEventDto newEventDto);

    EventFullDto patchEvent(Integer userId, NewEventDto newEventDto, Long eventId);

    EventFullDto getEventForCreator(Integer userId, Long eventId);

    List<ParticipationRequestDto> getEventRequests(Long eventId, Integer userId);

    EventRequestStatusUpdateResult updateEventRequestsStatus(Long eventId, Integer userId,
                                                             RequestStatusUpdateDto requestStatusUpdateDto);

    List<EventFullDto> getEventsForAdmin(Integer[] categories, Integer[] users, String[] states, String rangeStart, String rangeEnd,
                                         Integer from, Integer size);

    EventFullDto patchEventForAdmin(Long eventId, NewEventDto newEventDto);
}
