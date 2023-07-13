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
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.CategoryModel;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.service.CategoryService;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    public static final Type CATEGORIES_LIST_TYPE = 
            new TypeToken<List<CategoryModel>>() {}.getType();
    
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryModel create(CategoryModel model) throws ServiceException {
        try {
            Category entity = modelMapper.map(model, Category.class);
            Category persistedEntity = categoryRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, CategoryModel.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_CREATE_ERROR, e);
        }
    }

    @Override
    public List<CategoryModel> getAll() throws ServiceException {
        try {
            List<Category> entities = categoryRepository.findAll();
            return modelMapper.map(entities, CATEGORIES_LIST_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_FETCH_ERROR, e);
        }
    }

    @Override
    public CategoryModel update(CategoryModel model) throws ServiceException {
        try {
            Category entity = categoryRepository.findById(model.getName())
                    .orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            entity.setName(model.getName());
            Category updatedEntity = categoryRepository.saveAndFlush(entity);
            return modelMapper.map(updatedEntity, CategoryModel.class);
        } catch (DataAccessException e) {
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
}
