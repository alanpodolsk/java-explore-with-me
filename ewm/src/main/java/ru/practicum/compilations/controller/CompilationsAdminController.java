package ru.practicum.compilations.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patchCompilation(@RequestBody NewCompilationDto newCompilationDto, @PathVariable Integer compId) {
        return compilationService.patchCompilation(newCompilationDto, compId);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compId) {
        compilationService.deleteCompilation(compId);
    }
}
