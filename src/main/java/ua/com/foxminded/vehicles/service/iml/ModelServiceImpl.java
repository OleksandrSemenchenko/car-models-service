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
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.ModelModel;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.service.ModelService;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {
    
    public static final Type MODELS_LIST_TYPE = new TypeToken<List<ModelModel>>() {}.getType();
    
    private final ModelRepository modelRepository;
    private final ModelMapper modelMapper;

    @Override
    public ModelModel create(ModelModel model) throws ServiceException {
        try {
            Model entity = modelMapper.map(model, Model.class);
            Model persistedEntity = modelRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, ModelModel.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(MODEL_CREATE_ERROR, e);
        }
    }

    @Override
    public List<ModelModel> getAll() throws ServiceException {
        try {
            List<Model> entities = modelRepository.findAll();
            return modelMapper.map(entities, MODELS_LIST_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(CATEGORY_FETCH_ERROR, e);
        }
    }

    @Override
    public ModelModel update(ModelModel model) throws ServiceException {
        try {
            Model entity = modelRepository.findById(model.getName())
                    .orElseThrow(() -> new ServiceException(MODEL_ABSENCE));
            
            entity.setName(model.getName());
            Model updatedEntity = modelRepository.saveAndFlush(entity);
            return modelMapper.map(updatedEntity, ModelModel.class);
        } catch (DataAccessException | IllegalArgumentException | 
                MappingException | ConfigurationException e) {
            throw new ServiceException(MODEL_UPDATE_ERROR, e);
        }
    }

    @Override
    public void deleteByName(String name) throws ServiceException {
        try {
            modelRepository.findById(name).orElseThrow(() -> new ServiceException(MODEL_ABSENCE));
            modelRepository.deleteById(name);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ServiceException(MODEL_DELETE_ERROR, e);
        }
    }
}
