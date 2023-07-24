package ua.com.foxminded.vehicles.service;

import static ua.com.foxminded.vehicles.exception.ErrorCode.CATEGORY_ABSENCE;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.mapper.CategoryMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto save(CategoryDto model) {
            Category entity = categoryMapper.map(model);
            Category persistedEntity = categoryRepository.saveAndFlush(entity);
            return categoryMapper.map(persistedEntity);
    }

    public Page<CategoryDto> getAll(Pageable pageable) {
            return categoryRepository.findAll(pageable).map(categoryMapper::map);
    }

    public void deleleteByName(String name) {
            categoryRepository.findById(name).orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            categoryRepository.deleteById(name);
    }
    
    public CategoryDto getByName(String name) {
            Category entity = categoryRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            return categoryMapper.map(entity);
    }
}
