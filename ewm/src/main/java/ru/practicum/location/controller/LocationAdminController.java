package ru.practicum.location.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.dto.UpdateLocationDto;
import ru.practicum.location.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/admin/locations")
@AllArgsConstructor
public class LocationAdminController {

    private final LocationService locationService;
    @GetMapping
    public List<LocationDto> getAllLocations (@RequestParam (defaultValue = "0")Integer from, @RequestParam (defaultValue = "10") Integer size){
        return locationService.getAllLocations(from,size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto createLocation(@RequestBody LocationDto locationDto){
        return locationService.createLocation(locationDto);
    }

    @PatchMapping("/{locId}")
    public LocationDto patchLocation(@RequestBody UpdateLocationDto updateLocationDto, @PathVariable Long locId){
        return locationService.patchLocation(updateLocationDto, locId);
    }



}
