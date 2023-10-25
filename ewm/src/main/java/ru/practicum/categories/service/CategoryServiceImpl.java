package ru.practicum.categories.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.CategoryMapper;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.categories.model.Category;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.exception.NoObjectException;
import ru.practicum.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            throw new ValidationException("Empty category object in POST operation");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public CategoryDto patchCategory(Integer id, NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            throw new ValidationException("Empty category object in PATCH operation");
        }
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            throw new NoObjectException("Category with id = " + id + " was not found");
        }
        Category category = optionalCategory.get();
        if (!newCategoryDto.getName().equals(category.getName())) {
            category.setName(newCategoryDto.getName());
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Integer id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new NoObjectException("Category with id = " + id + " was not found");
        }
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return CategoryMapper.toCategoryDtoList(categoryRepository.getAll(PageRequest.of(from / size, size)).getContent());
    }

    @Override
    public CategoryDto getCategoryById(Integer catId) {
        Optional<Category> optCategory = categoryRepository.findById(catId);
        if (optCategory.isPresent()) {
            return CategoryMapper.toCategoryDto(optCategory.get());
        } else {
            throw new NoObjectException("Category with id = " + catId + " was not found");
        }
    }
}
