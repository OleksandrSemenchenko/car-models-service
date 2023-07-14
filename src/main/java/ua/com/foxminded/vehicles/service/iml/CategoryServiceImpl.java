package ua.com.foxminded.vehicles.service.iml;

import static ua.com.foxminded.vehicles.exception.ErrorCode.*;

import java.lang.reflect.Type;
import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.entity.CategoryEntity;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.Category;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.service.CategoryService;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    public static final Type CATEGORIES_LIST_TYPE = 
            new TypeToken<List<Category>>() {}.getType();
    
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public Category save(Category model) {
        try {
            CategoryEntity entity = modelMapper.map(model, CategoryEntity.class);
            CategoryEntity persistedEntity = categoryRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, Category.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_CREATE_ERROR, e);
        }
    }

    @Override
    public List<Category> getAll() {
        try {
            List<CategoryEntity> entities = categoryRepository.findAll();
            return modelMapper.map(entities, CATEGORIES_LIST_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_FETCH_ERROR, e);
        }
    }

    @Override
    public Category updateName(String newName, String oldName) {
        try {
            categoryRepository.findById(oldName).orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            categoryRepository.updateName(newName, oldName);
            CategoryEntity updatedEntity = categoryRepository.findById(newName).orElseThrow();
            return modelMapper.map(updatedEntity, Category.class);
        } catch (DataAccessException | IllegalArgumentException | 
                MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleleteByName(String name) {
        try {
            categoryRepository.findById(name).orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            categoryRepository.deleteById(name);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ServiceException(CATEGORY_DELETE_ERROR, e);
        }
    }
    
    @Override
    public Category getByName(String name) {
        try {
            CategoryEntity entity = categoryRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            return modelMapper.map(entity, Category.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_FETCH_ERROR, e);
        }
    }
}
