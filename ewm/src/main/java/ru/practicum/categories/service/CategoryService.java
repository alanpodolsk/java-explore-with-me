package ru.practicum.categories.service;

import org.springframework.validation.annotation.Validated;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(@Validated NewCategoryDto newCategoryDto);

    CategoryDto patchCategory(Integer id, @Validated NewCategoryDto newCategoryDto);

    void deleteCategory(Integer id);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Integer catId);
}
