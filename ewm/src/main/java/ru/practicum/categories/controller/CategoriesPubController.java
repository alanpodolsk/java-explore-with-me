package ru.practicum.categories.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.service.CategoryService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@AllArgsConstructor
public class CategoriesPubController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@Validated @RequestParam(defaultValue = "0") @Min(0) Integer from, @Validated @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }

}
