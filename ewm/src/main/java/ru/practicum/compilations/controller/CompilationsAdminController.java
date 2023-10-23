package ru.practicum.compilations.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.service.CompilationService;

@RestController
@RequestMapping(path = "/admin/compilations")
@AllArgsConstructor
public class CompilationsAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto){
        return compilationService.createCompilation (newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto, @PathVariable Integer compId){
        return compilationService.patchCompilation (newCompilationDto, compId);
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Integer compId){
        compilationService.deleteCompilation (compId);
    }



}
