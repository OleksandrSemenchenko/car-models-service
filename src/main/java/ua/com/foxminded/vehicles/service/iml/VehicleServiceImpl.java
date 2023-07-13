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
import ua.com.foxminded.vehicles.entity.Vehicle;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.VehicleModel;
import ua.com.foxminded.vehicles.repository.VehicleRepository;
import ua.com.foxminded.vehicles.service.VehicleService;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    
    public static final Type VEHICLES_LIST_TYPE = new TypeToken<List<VehicleModel>>() {}.getType();
    
    private final VehicleRepository vehicleRepository;
    private final ModelMapper modelMapper;
    
    @Override
    public VehicleModel create(VehicleModel model) throws ServiceException {
        try {
            Vehicle entity = modelMapper.map(model, Vehicle.class);
            Vehicle persistedEntity = vehicleRepository.saveAndFlush(entity);
            return modelMapper.map(persistedEntity, VehicleModel.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_CREATE_ERROR, e);
        }
    }
    @Override
    public List<VehicleModel> getAll() throws ServiceException {
        try {
            List<Vehicle> entities = vehicleRepository.findAll();
            return modelMapper.map(entities, VEHICLES_LIST_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
    
    @Override
    public VehicleModel update(VehicleModel model) throws ServiceException {
        try {
            Vehicle entity = vehicleRepository.findById(model.getId())
                    .orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            entity.setId(model.getId());
            entity.setProductionYear(model.getProductionYear());
            Vehicle updatedEntity = vehicleRepository.saveAndFlush(entity);
            return modelMapper.map(updatedEntity, VehicleModel.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_UPDATE_ERROR, e);
        }
    }
    
    @Override 
    public void deleteById(String id) throws ServiceException {
        try {
            vehicleRepository.findById(id).orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            vehicleRepository.deleteById(id);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ServiceException(VEHICLE_DELETE_ERROR, e);
        }
    }
}
