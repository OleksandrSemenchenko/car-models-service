package ua.com.foxminded.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.dto.CategoryDto;
import ua.com.foxminded.entity.Category;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exception.NotFoundException;
import ua.com.foxminded.mapper.CategoryMapper;
import ua.com.foxminded.repository.CategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    public static final String NO_CATEGORY = "The category '%s' doesn't exist";
    public static final String CATEGORY_ALREADY_EXISTS = "The category '%s' already exists";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    public CategoryDto save(CategoryDto categoryDto) {
        if (categoryRepository.existsById(categoryDto.getName())) {
            throw new AlreadyExistsException(String.format(CATEGORY_ALREADY_EXISTS, categoryDto.getName()));
        }
        
        Category category = categoryMapper.map(categoryDto);
        Category persistedCategory = categoryRepository.save(category);
        return categoryMapper.map(persistedCategory);
    }

    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(categoryMapper::map);
    }

    public void deleleteByName(String name) {
        categoryRepository.findById(name).orElseThrow(() -> new NotFoundException(String.format(NO_CATEGORY, name)));
        categoryRepository.deleteById(name);
    }

    public Optional<CategoryDto> getByName(String name) {
        return categoryRepository.findById(name).map(categoryMapper::map);
    }
}
