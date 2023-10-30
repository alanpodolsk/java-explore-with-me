package ru.practicum.location.service;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.UpdateLocationDto;

import java.util.List;

public interface LocationService {

    List<LocationDto> getAllLocations (Integer from, Integer size);
    LocationDto createLocation(LocationDto locationDto);
    LocationDto patchLocation(UpdateLocationDto updateLocationDto, Long locId);
}
