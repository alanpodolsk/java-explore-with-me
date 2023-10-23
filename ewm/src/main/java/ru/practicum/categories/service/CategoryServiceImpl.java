package ru.practicum.categories.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.repository.CategoryRepository;

@Component
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return null;
    }

    @Override
    public CategoryDto patchCategory(Integer id, NewCategoryDto newCategoryDto) {
        return null;
    }

    @Override
    public void deleteCategory(Integer id) {

    }
}
