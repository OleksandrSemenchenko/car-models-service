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
import ua.com.foxminded.vehicles.entity.VehicleEntity;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.Vehicle;
import ua.com.foxminded.vehicles.repository.VehicleRepository;
import ua.com.foxminded.vehicles.service.VehicleService;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    
    public static final Type VEHICLES_LIST_TYPE = new TypeToken<List<Vehicle>>() {}.getType();
    
    private final VehicleRepository vehicleRepository;
    private final ModelMapper modelMapper;
    
    @Override
    public Vehicle save(Vehicle model) {
        try {
            VehicleEntity entity = modelMapper.map(model, VehicleEntity.class);
            VehicleEntity persistedEntity = vehicleRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, Vehicle.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_CREATE_ERROR, e);
        }
    }
    @Override
    public List<Vehicle> getAll() {
        try {
            List<VehicleEntity> entities = vehicleRepository.findAll();
            return modelMapper.map(entities, VEHICLES_LIST_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
    
    @Override
    public Vehicle update(Vehicle model) {
        try {
            VehicleEntity entity = vehicleRepository.findById(model.getId())
                    .orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            entity.setId(model.getId());
            entity.setProductionYear(model.getProductionYear());
            VehicleEntity updatedEntity = vehicleRepository.saveAndFlush(entity);
            return modelMapper.map(updatedEntity, Vehicle.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_UPDATE_ERROR, e);
        }
    }
    
    @Override 
    public void deleteById(String id) {
        try {
            vehicleRepository.findById(id).orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            vehicleRepository.deleteById(id);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ServiceException(VEHICLE_DELETE_ERROR, e);
        }
    }
    
    @Override
    public Vehicle getById(String id) {
        try {
            VehicleEntity entity = vehicleRepository.findById(id)
                    .orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            return modelMapper.map(entity, Vehicle.class);
        } catch (DataAccessException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
}
