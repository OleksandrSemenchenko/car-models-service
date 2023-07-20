package ua.com.foxminded.vehicles.service;

import static ua.com.foxminded.vehicles.exception.ErrorCode.CATEGORY_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.CATEGORY_CREATE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.CATEGORY_DELETE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.CATEGORY_FETCH_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.CATEGORY_UPDATE_ERROR;

import java.lang.reflect.Type;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.repository.CategoryRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    
    public static final Type CATEGORIES_PAGE_TYPE = 
            new TypeToken<Page<CategoryDto>>() {}.getType();
    
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryDto save(CategoryDto model) {
        try {
            Category entity = modelMapper.map(model, Category.class);
            Category persistedEntity = categoryRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, CategoryDto.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_CREATE_ERROR, e);
        }
    }

    public Page<CategoryDto> getAll(Pageable pageable) {
        try {
            Page<Category> entities = categoryRepository.findAll(pageable);
            return modelMapper.map(entities, CATEGORIES_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_FETCH_ERROR, e);
        }
    }

    public CategoryDto updateName(String newName, String oldName) {
        try {
            categoryRepository.findById(oldName).orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            categoryRepository.updateName(newName, oldName);
            Category updatedEntity = categoryRepository.findById(newName).orElseThrow();
            return modelMapper.map(updatedEntity, CategoryDto.class);
        } catch (DataAccessException | IllegalArgumentException | 
                MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_UPDATE_ERROR, e);
        }
    }

    public void deleleteByName(String name) {
        try {
            categoryRepository.findById(name).orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            categoryRepository.deleteById(name);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ServiceException(CATEGORY_DELETE_ERROR, e);
        }
    }
    
    public CategoryDto getByName(String name) {
        try {
            Category entity = categoryRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            return modelMapper.map(entity, CategoryDto.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_FETCH_ERROR, e);
        }
    }
}
