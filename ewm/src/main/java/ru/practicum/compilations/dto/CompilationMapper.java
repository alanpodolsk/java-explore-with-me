package ru.practicum.compilations.dto;

import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.dto.EventShortDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(
                null,
                newCompilationDto.getPinned(),
                newCompilationDto.getTitle(),
                null
        );
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        List<EventShortDto> events;
        if (compilation.getEvents() != null) {
            events = EventMapper.toEventShortDtoList(new ArrayList<>(compilation.getEvents()));
        } else {
            events = null;
        }
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle(),
                events
        );
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilations) {
        if (compilations == null) {
            return null;
        }
        return compilations.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }
}
