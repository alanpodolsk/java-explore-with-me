package ru.practicum.compilations.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.repository.CompilationRepository;

import java.util.List;
@Component
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService{

    private final CompilationRepository compilationRepository;
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        return null;
    }

    @Override
    public CompilationDto patchCompilation(NewCompilationDto newCompilationDto, Integer compId) {
        return null;
    }

    @Override
    public void deleteCompilation(Integer compId) {

    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        return null;
    }
}
