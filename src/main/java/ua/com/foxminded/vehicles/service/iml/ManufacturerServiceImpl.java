package ua.com.foxminded.vehicles.service.iml;

import static ua.com.foxminded.vehicles.exception.ErrorCode.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import org.hibernate.boot.MappingException;
import org.modelmapper.ConfigurationException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.modelmapper.TypeToken;
import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.ManufacturerModel;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.service.ManufacturerService;

@Service
@Transactional
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {
    
    public static final Type MANUFACTURERS_LIST_TYPE = new TypeToken<List<ManufacturerModel>>() {}.getType();
    
    private final ModelMapper modelMapper;
    private final ManufacturerRepository manufacturerRepository;
    
    @Override
    public ManufacturerModel create(ManufacturerModel model) throws ServiceException {
        try {
            Manufacturer entity = modelMapper.map(model, Manufacturer.class);
            Manufacturer persistedEntity = manufacturerRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, ManufacturerModel.class);
        } catch (DataAccessException | IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_CREATE_ERROR, e);
        }
    }
    
    @Override
    public List<ManufacturerModel> getAll() throws ServiceException {
        try {
            List<Manufacturer> entities = manufacturerRepository.findAll();
            return modelMapper.map(entities, MANUFACTURERS_LIST_TYPE);
            
        } catch (DataAccessException | IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_FETCH_ERROR, e);
        }
    }
    
    @Override
    public ManufacturerModel update(ManufacturerModel model) throws ServiceException {
        try {
            Optional<Manufacturer> optionalEntity = manufacturerRepository.findById(model.getName());
            
            if (optionalEntity.isPresent()) {
                Manufacturer entity = optionalEntity.get();
                entity.setName(model.getName());
                manufacturerRepository.saveAndFlush(entity);
                return modelMapper.map(entity, ManufacturerModel.class);
            } else {
                throw new ServiceException(MANUFACTURER_ABSENCE);
            }
        } catch (DataAccessException | IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException(MANUFACTURER_UPDATE_ERROR, e);
        }
    }
    
    @Override
    public void deleteByName(String name) throws ServiceException {
        try {
            manufacturerRepository.findById(name).orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            manufacturerRepository.deleteById(name);
        } catch (DataAccessException e) {
            throw new ServiceException(MANUFACTURER_DELETE_ERROR);
        }
    }
}
