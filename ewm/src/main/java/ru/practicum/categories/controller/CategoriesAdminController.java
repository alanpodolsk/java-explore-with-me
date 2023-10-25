package ru.practicum.categories.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.service.CategoryService;

@RestController
@RequestMapping(path = "/admin/categories")
@AllArgsConstructor
public class CategoriesAdminController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return categoryService.createCategory(newCategoryDto);
    }

    @PatchMapping("/{id}")
    public CategoryDto patchCategory(@PathVariable Integer id, NewCategoryDto newCategoryDto) {
        return categoryService.patchCategory(id, newCategoryDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
    }

}
