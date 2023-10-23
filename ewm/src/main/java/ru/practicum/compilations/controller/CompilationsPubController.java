package ru.practicum.compilations.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.compilations.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping(path = "/compilations")
@AllArgsConstructor
public class CompilationsPubController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "") Boolean pinned, @RequestParam (defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size){
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Integer compId){
        return compilationService.getCompilationById(compId);
    }
}
