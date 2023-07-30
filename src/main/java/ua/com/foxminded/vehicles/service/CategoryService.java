package ua.com.foxminded.vehicles.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.mapper.CategoryMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    public static final String NO_CATEGORY = "The category \"%s\" doesn't exist";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    public boolean existsByName(String name) {
        return categoryRepository.existsById(name);
    }

    public CategoryDto save(CategoryDto categoryDto) {
        Category category = categoryMapper.map(categoryDto);
        Category persistedCategory = categoryRepository.saveAndFlush(category);
        return categoryMapper.map(persistedCategory);
    }

    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                                 .map(categoryMapper::map);
    }

    public void deleleteByName(String name) {
        categoryRepository.findById(name).orElseThrow(
                () -> new NoSuchElementException(String.format(NO_CATEGORY, name)));
        categoryRepository.deleteById(name);
    }

    public CategoryDto getByName(String name) {
        Category category = categoryRepository.findById(name).orElseThrow(
                () -> new NoSuchElementException(String.format(NO_CATEGORY, name)));
        return categoryMapper.map(category);
    }
}
