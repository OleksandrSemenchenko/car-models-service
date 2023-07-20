package ua.com.foxminded.vehicles.service;

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
import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.repository.ModelRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelService {
    
    public static final Type MODELS_PAGE_TYPE = new TypeToken<Page<ModelDto>>() {}.getType();
    
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    public ModelDto save(ModelDto model) {
        try {
            Model entity = modelMapper.map(model, Model.class);
            Model persistedEntity = modelRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, ModelDto.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(MODEL_CREATE_ERROR, e);
        }
    }

    public Page<ModelDto> getAll(Pageable pageable) {
        try {
            Page<Model> entities = modelRepository.findAll(pageable);
            return modelMapper.map(entities, MODELS_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_FETCH_ERROR, e);
        }
    }

    public ModelDto updateName(String newName, String oldName) {
        try {
            modelRepository.findById(oldName).orElseThrow(() -> new ServiceException(MODEL_ABSENCE));
            modelRepository.updateName(newName, oldName);
            Model updatedEntity = modelRepository.findById(oldName).orElseThrow();
            return modelMapper.map(updatedEntity, ModelDto.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(MODEL_UPDATE_ERROR, e);
        }
    }

    public void deleteByName(String name) {
        try {
            modelRepository.findById(name).orElseThrow(() -> new ServiceException(MODEL_ABSENCE));
            modelRepository.deleteById(name);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ServiceException(MODEL_DELETE_ERROR, e);
        }
    }
    
    public ModelDto getByName(String name) {
        try {
            Model entity = modelRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            ModelDto model = modelMapper.map(entity, ModelDto.class);
            return model;
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
}
