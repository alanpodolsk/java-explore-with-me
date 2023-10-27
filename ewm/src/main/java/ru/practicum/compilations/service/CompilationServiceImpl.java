package ru.practicum.compilations.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.CompilationMapper;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.compilations.repository.EventsCompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NoObjectException;

import java.util.List;
import java.util.Optional;

@Validated
@Component
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventsCompilationRepository eventsCompilationRepository;
    private final EventRepository eventRepository;

    @Override
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
    public CompilationDto patchCompilation(@RequestBody NewCompilationDto newCompilationDto, Integer compId) {
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
            compilations = compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
        //TODO
        return CompilationMapper.toCompilationDtoList(compilations);
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        Optional<Compilation> compilationOpt = compilationRepository.findById(compId);
        if (compilationOpt.isPresent()) {
            return CompilationMapper.toCompilationDto(compilationOpt.get());
        } else {
            throw new NoObjectException(String.format("Compilation with id = %s was not found", compId));
        }
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
