package ua.com.foxminded.vehicles.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.exception.AlreadyExistsException;
import ua.com.foxminded.vehicles.exception.NotFoundException;
import ua.com.foxminded.vehicles.mapper.CategoryMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    public static final String NO_CATEGORY = "The category '%s' doesn't exist";
    public static final String CATEGORY_ALREADY_EXISTS = "The category '%s' already exists";

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    
    public boolean existsByName(String name) {
        return categoryRepository.existsById(name);
    }

    public CategoryDto save(CategoryDto categoryDto) {
        if (categoryRepository.existsById(categoryDto.getName())) {
            throw new AlreadyExistsException(String.format(CATEGORY_ALREADY_EXISTS, categoryDto.getName()), 
                                             HttpStatus.CONFLICT);
        }
        
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
                () -> new NotFoundException(String.format(NO_CATEGORY, name), HttpStatus.NOT_FOUND));
        categoryRepository.deleteById(name);
    }

    public CategoryDto getByName(String name) {
        Category category = categoryRepository.findById(name).orElseThrow(
                () -> new NotFoundException(String.format(NO_CATEGORY, name), HttpStatus.NOT_FOUND));
        return categoryMapper.map(category);
    }
}
