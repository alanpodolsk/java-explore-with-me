package ru.practicum.compilations.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.compilations.repository.EventsCompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NoObjectException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@Component
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventsCompilationRepository eventsCompilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(@Valid NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (newCompilationDto.getEvents() != null) {
            addEventsToCompilation(newCompilationDto.getEvents(), compilationDto.getId());
        }
        //compilationDto.setEvents() TODO сделать метод репозитория для выгрузки по перечню id и конвертер в лист EventShortDto
        return compilationDto;
    }

    @Override
    public CompilationDto patchCompilation(NewCompilationDto newCompilationDto, Integer compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NoObjectException("Compilation with id = " + compId + "not found");
        }
        Compilation compilation = compilationRepository.findById(compId).get();
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (newCompilationDto.getEvents() != null) {
            eventsCompilationRepository.deleteRowsByCompId(compId);
        }
        //compilationDto.setEvents() TODO сделать метод репозитория для выгрузки по перечню id и конвертер в лист EventShortDto
        return compilationDto;
    }

    @Override
    public void deleteCompilation(Integer compId) {
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
        } else {
            throw new NoObjectException("Compilation with id =" + compId + " was not found");
        }
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.getByPinned(pinned, PageRequest.of(from / size, size)).getContent();
        } else {
            compilations = compilationRepository.getAll(PageRequest.of(from / size, size)).getContent();
        }
        List<CompilationDto> compilationDtos = CompilationMapper.toCompilationDtoList(compilations);
        //TODO
        return null;
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        return null;
    }

    private void addEventsToCompilation(List<Long> eventIds, Integer compId) {
        for (Long event : eventIds) {
            if (eventRepository.existsById(event)) {
                eventsCompilationRepository.addEventToCompilation(event, compId);
            } else {
                throw new NoObjectException("Event with id " + event + " was not found");
            }
        }
    }

    private List<CompilationDto> addEventsToCompilations(List<CompilationDto> compilationDtos) {
        List<Integer> compIds = compilationDtos.stream().map(CompilationDto::getId).collect(Collectors.toList());
        //TODO eventRepository.getEventsByCompId
        //TODO EventMapper.toEventShortDtoList
        return compilationDtos;
    }
}
