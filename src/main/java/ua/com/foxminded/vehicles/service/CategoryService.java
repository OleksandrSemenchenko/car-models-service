package ua.com.foxminded.vehicles.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;

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
    
    public static final String CATEGORY_IS_PRESENT = "The category \"%s\" already exists";
    public static final String CATEGORY_IS_NOT_PRESENT = "The category \"%s\" doesn't exist";
    
    
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryDto save(CategoryDto model) {
            if (categoryRepository.findById(model.getName()).isPresent()) {
               throw new ServiceException(String.format(CATEGORY_IS_PRESENT, model.getName()), BAD_REQUEST); 
            }
            
            Category entity = categoryMapper.map(model);
            Category persistedEntity = categoryRepository.saveAndFlush(entity);
            return categoryMapper.map(persistedEntity);
    }

    public Page<CategoryDto> getAll(Pageable pageable) {
            return categoryRepository.findAll(pageable).map(categoryMapper::map);
    }

    public void deleleteByName(String name) {
            categoryRepository.findById(name).orElseThrow(
                    () -> new ServiceException(String.format(CATEGORY_IS_NOT_PRESENT, name), NO_CONTENT));
            categoryRepository.deleteById(name);
    }
    
    public CategoryDto getByName(String name) {
            Category entity = categoryRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(String.format(CATEGORY_IS_NOT_PRESENT, name), 
                                                            BAD_REQUEST));
            return categoryMapper.map(entity);
    }
}
