package ru.practicum.compilations.dto;

import ru.practicum.compilations.model.Compilation;

public class CompilationMapper {
    public static Compilation toCompilation (NewCompilationDto newCompilationDto){
        return new Compilation(
                null,
                newCompilationDto.getPinned(),
                newCompilationDto.getTitle()
        );
    }

    public static CompilationDto toCompilationDto (Compilation compilation){
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle(),
                null
        );
    }
}
