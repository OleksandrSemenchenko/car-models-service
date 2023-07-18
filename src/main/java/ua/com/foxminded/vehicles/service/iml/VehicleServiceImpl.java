package ua.com.foxminded.vehicles.service.iml;

import static ua.com.foxminded.vehicles.exception.ErrorCode.CATEGORY_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.MANUFACTURER_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.VEHICLE_ABSENCE;
import static ua.com.foxminded.vehicles.exception.ErrorCode.VEHICLE_CREATE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.VEHICLE_DELETE_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.VEHICLE_FETCH_ERROR;
import static ua.com.foxminded.vehicles.exception.ErrorCode.VEHICLE_UPDATE_ERROR;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import ua.com.foxminded.vehicles.entity.CategoryEntity;
import ua.com.foxminded.vehicles.entity.VehicleEntity;
import ua.com.foxminded.vehicles.exception.ErrorCode;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.model.Category;
import ua.com.foxminded.vehicles.model.Vehicle;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.repository.VehicleRepository;
import ua.com.foxminded.vehicles.service.VehicleService;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    
    public static final Type VEHICLES_PAGE_TYPE = new TypeToken<Page<Vehicle>>() {}.getType();
    
    private final VehicleRepository vehicleRepository;
    private final CategoryRepository categoryRepository;
    private final ModelRepository modelRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final ModelMapper modelMapper;
    
    @Override
    public Page<Vehicle> getByCategory(String categoryName, Pageable pageable) {
        try {
            categoryRepository.findById(categoryName).orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            Page<VehicleEntity> vehicles = vehicleRepository.findByCategoriesName(categoryName, pageable);
            return modelMapper.map(vehicles, VEHICLES_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
    
    @Override
    public Page<Vehicle> getByModel(String modelName, Pageable pageable) {
        try {
            modelRepository.findById(modelName)
                           .orElseThrow(() -> new ServiceException(ErrorCode.MODEL_ABSENCE));
            Page<VehicleEntity> vehicles = vehicleRepository.findByModelName(modelName, pageable);
            return modelMapper.map(vehicles, VEHICLES_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR);
        }
    }
    
    @Override
    public Page<Vehicle> getByManufacturerNameAndMaxYear(String manufacturerName, 
                                                         int maxYear, 
                                                         Pageable pageable) {
        try {
            manufacturerRepository.findById(manufacturerName)
                                  .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            Page<VehicleEntity> vehicles = vehicleRepository
                    .findByManufacturerNameAndProductionYearLessThanEqual(
                            manufacturerName, maxYear, pageable);
            return modelMapper.map(vehicles, VEHICLES_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
    
    @Override
    public Page<Vehicle> getByManufacturerNameAndMinYear(String manufacturerName, 
                                                         int minYear, 
                                                         Pageable pageable) {
        try {
            manufacturerRepository.findById(manufacturerName)
                                  .orElseThrow(() -> new ServiceException(ErrorCode.MANUFACTURER_ABSENCE));
            Page<VehicleEntity> vehiclesPage = vehicleRepository
                    .findByManufacturerNameAndProductionYearGreaterThanEqual(manufacturerName, minYear, pageable);
            return modelMapper.map(vehiclesPage, VEHICLES_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
    
    @Override
    public Vehicle save(Vehicle vehicle) {
        try {
            var vehicleEntity = VehicleEntity.builder()
                    .productionYear(vehicle.getProductionYear()).build();
            
            if (vehicle.hasCategories()) {
                vehicleEntity.setCategories(new HashSet<>());
                
                for (Category category : vehicle.getCategories()) {
                    var categoryEntity = categoryRepository.findById(category.getName())
                            .orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
                    vehicleEntity.addCategory(categoryEntity);
                }
            } else if (vehicle.hasManufacturer()) {
                var manufacturerEntity = manufacturerRepository.findById(vehicle.getManufacturer().getName())
                        .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
                vehicleEntity.setManufacturer(manufacturerEntity);
            } else if (vehicle.hasModel()) {
                var modelEntity = modelRepository.findById(vehicle.getModel().getName())
                        .orElseThrow(() -> new ServiceException(ErrorCode.MODEL_ABSENCE));
                vehicleEntity.setModel(modelEntity);
            }
            
            VehicleEntity persistedVehicle = vehicleRepository.saveAndFlush(vehicleEntity);
            
            return modelMapper.map(persistedVehicle, Vehicle.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_CREATE_ERROR, e);
        }
    }
    
    @Override
    public Page<Vehicle> getAll(Pageable pageable) {
        try {
            Page<VehicleEntity> entities = vehicleRepository.findAll(pageable);
            return modelMapper.map(entities, VEHICLES_PAGE_TYPE);
        } catch (DataAccessException | IllegalArgumentException | 
                 MappingException | ConfigurationException e) {
            throw new ServiceException(VEHICLE_FETCH_ERROR, e);
        }
    }
    
    @Override
    public Vehicle update(Vehicle vehicle) {
        try {
            var vehicleEntity = vehicleRepository.findById(vehicle.getId())
                    .orElseThrow(() -> new ServiceException(VEHICLE_ABSENCE));
            
            vehicleEntity.setProductionYear(vehicle.getProductionYear());
            updateManufacturer(vehicle, vehicleEntity);
            updateModel(vehicle, vehicleEntity);
            updateCategories(vehicle, vehicleEntity);
           
            VehicleEntity updatedVehicle = vehicleRepository.saveAndFlush(vehicleEntity);
            return modelMapper.map(updatedVehicle, Vehicle.class);
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
    
    private void updateCategories(Vehicle vehicle, VehicleEntity vehicleEntity) {
        if (vehicle.getCategories() !=null && vehicleEntity.getCategories() != null) {
            List<CategoryEntity> unnecessaryRelations = vehicleEntity.getCategories().stream()
                    .filter(categoryEntity -> {
                        return vehicle.getCategories().stream()
                                .noneMatch(category -> categoryEntity.getName().equals(category.getName()));
                    }).toList();

            unnecessaryRelations.stream().forEach(vehicleEntity::removeCategory);

            Set<Category> necessaryRelations = vehicle.getCategories().stream().filter(category -> {
                return vehicleEntity.getCategories().stream()
                        .noneMatch(categoryEntity -> category.getName().equals(categoryEntity.getName()));
            }).collect(Collectors.toSet());

            addCategories(necessaryRelations, vehicleEntity);
        } else if (vehicle.getCategories() == null && vehicleEntity.getCategories() != null) {
            Set<CategoryEntity> unnecessaryRelations = vehicleEntity.getCategories();
            unnecessaryRelations.stream().forEach(vehicleEntity::removeCategory);
        } else if (vehicle.getCategories() != null) {
            Set<Category> necessaryRelations = vehicle.getCategories();
            addCategories(necessaryRelations, vehicleEntity);
        }
    }
    
    private void addCategories(Set<Category> categories, VehicleEntity vehicleEntity) {
        for (Category category : categories) {
            var categoryEntity = categoryRepository.findById(category.getName())
                    .orElseThrow(() -> new ServiceException(CATEGORY_ABSENCE));
            vehicleEntity.addCategory(categoryEntity);
        }
    }
    
    private void updateModel(Vehicle vehicle, VehicleEntity vehicleEntity) {
        if (vehicle.hasModel()) {
            var modelEntity = modelRepository.findById(vehicle.getModel().getName())
                    .orElseThrow(() -> new ServiceException(ErrorCode.MODEL_ABSENCE));
            vehicleEntity.setModel(modelEntity);
        } else {
            vehicleEntity.setModel(null);
        }
    }
    
    private void updateManufacturer(Vehicle vehicle, VehicleEntity vehicleEntity) {
        if (vehicle.hasManufacturer()) {
            var manufacturerEntity = manufacturerRepository.findById(vehicle.getManufacturer().getName())
                    .orElseThrow(() -> new ServiceException(MANUFACTURER_ABSENCE));
            vehicleEntity.setManufacturer(manufacturerEntity);
        } else {
            vehicleEntity.setManufacturer(null);
        }
    }
}
