package ua.com.foxminded.vehicles.service.iml;

import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_CREATE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_DELETE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_FETCH_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_UPDATE_ERROR;

import java.lang.reflect.Type;
import java.util.List;

import org.hibernate.boot.MappingException;
import org.modelmapper.ConfigurationException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.entity.ManufacturerEntity;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.Manufacturer;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.service.ManufacturerService;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {
    
    public static final Type MANUFACTURERS_LIST_TYPE = new TypeToken<List<Manufacturer>>() {}.getType();
    
    private final ModelMapper modelMapper;
    private final ManufacturerRepository manufacturerRepository;
    
    @Override
    public Manufacturer save(Manufacturer model) {
        try {
            ManufacturerEntity entity = modelMapper.map(model, ManufacturerEntity.class);
            ManufacturerEntity persistedEntity = manufacturerRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, Manufacturer.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_CREATE_ERROR, e);
        }
    }
    
    @Override
    public List<Manufacturer> getAll() {
        try {
            List<ManufacturerEntity> entities = manufacturerRepository.findAll();
            return modelMapper.map(entities, MANUFACTURERS_LIST_TYPE);
            
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_FETCH_ERROR, e);
        }
    }
    
    @Override
    public Manufacturer updateName(String newName, String oldName) {
        try {
            manufacturerRepository.findById(oldName)
                .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            manufacturerRepository.updateName(newName, oldName);
            ManufacturerEntity updatedEntity = manufacturerRepository.findById(newName).orElseThrow();
            return modelMapper.map(updatedEntity, Manufacturer.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_UPDATE_ERROR, e);
        }
    }
    
    @Override
    public void deleteByName(String name) {
        try {
            manufacturerRepository.findById(name)
                .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            manufacturerRepository.deleteById(name);
        } catch (DataAccessException e) {
            throw new ServiceException(MANUFACTURER_DELETE_ERROR);
        }
    }
    
    @Override
    public Manufacturer getByName(String name) {
        try {
            ManufacturerEntity entity = manufacturerRepository.findById(name)
                    .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            return modelMapper.map(entity, Manufacturer.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_FETCH_ERROR, e);
        }
    }
}
