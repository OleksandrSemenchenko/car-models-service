package ua.com.foxminded.vehicles.service.iml;

import static ua.com.foxminded.vehicles.exception.ErrorCode.CATEGORY_FETCH_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MODEL_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MODEL_CREATE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MODEL_DELETE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MODEL_UPDATE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.VEHICLE_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.VEHICLE_FETCH_ERROR;

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
import ua.com.foxminded.vehicles.entity.ModelEntity;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.Model;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.service.ModelService;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    
    public static final Type MODELS_PAGE_TYPE = new TypeToken<Page<Model>>() {}.getType();
    
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    @Override
    public Model save(Model model) {
        try {
            ModelEntity entity = modelMapper.map(model, ModelEntity.class);
            ModelEntity persistedEntity = modelRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, Model.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(MODEL_CREATE_ERROR, e);
        }
    }

    @Override
    public Page<Model> getAll(Pageable pageable) {
        try {
            Page<ModelEntity> entities = modelRepository.findAll(pageable);
            return modelMapper.map(entities, MODELS_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_FETCH_ERROR, e);
        }
    }

    @Override
    public Model updateName(String newName, String oldName) {
        try {
            modelRepository.findById(oldName).orElseThrow(() -> new ServiceException(MODEL_ABSENCE));
            modelRepository.updateName(newName, oldName);
            ModelEntity updatedEntity = modelRepository.findById(oldName).orElseThrow();
            return modelMapper.map(updatedEntity, Model.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(MODEL_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteByName(String name) {
        try {
            modelRepository.findById(name).orElseThrow(() -> new ServiceException(MODEL_ABSENCE));
            modelRepository.deleteById(name);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ServiceException(MODEL_DELETE_ERROR, e);
        }
    }
    
    @Override
    public Model getByName(String name) {
        try {
            ModelEntity entity = modelRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            Model model = modelMapper.map(entity, Model.class);
            return model;
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
}
