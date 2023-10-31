package ru.practicum.compilations.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.compilations.repository.EventsCompilationRepository;
import ru.practicum.events.dto.EventMapper;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NoObjectException;

import java.util.List;
import java.util.Optional;

@Validated
@Component
@AllArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventsCompilationRepository eventsCompilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            throw new NoObjectException("Empty object in POST Request");
        }
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (newCompilationDto.getEvents() != null) {
            addEventsToCompilation(newCompilationDto.getEvents(), compilationDto.getId());
        }
        return getCompilationById(compilationDto.getId());
    }

    @Override
    @Transactional
    public CompilationDto patchCompilation(@RequestBody UpdateCompilationDto updateCompilationDto, Integer compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NoObjectException("Compilation with id = " + compId + "not found");
        }
        Compilation compilation = compilationRepository.findById(compId).get();
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        if (updateCompilationDto.getEvents() != null) {
            eventsCompilationRepository.deleteRowsByCompId(compId);
            addEventsToCompilation(updateCompilationDto.getEvents(), compilationDto.getId());
        }
        return getCompilationById(compilationDto.getId());
    }

    @Override
    @Transactional
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
            compilations = compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
        List<CompilationDto> compilationDtos = CompilationMapper.toCompilationDtoList(compilations);
        for (CompilationDto compilationDto : compilationDtos) {
            compilationDto.setEvents(EventMapper.toEventShortDtoList(eventRepository.findAllEventsInCompilation(compilationDto.getId())));
        }
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        Optional<Compilation> compilationOpt = compilationRepository.findById(compId);
        CompilationDto compilationDto;
        if (compilationOpt.isPresent()) {
            compilationDto = CompilationMapper.toCompilationDto(compilationOpt.get());
        } else {
            throw new NoObjectException(String.format("Compilation with id = %s was not found", compId));
        }
        compilationDto.setEvents(EventMapper.toEventShortDtoList(eventRepository.findAllEventsInCompilation(compId)));
        return compilationDto;
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
}
