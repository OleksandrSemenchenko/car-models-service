package ua.com.foxminded.vehicles.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static ua.com.foxminded.vehicles.service.CategoryService.CATEGORY_IS_NOT_PRESENT;
import static ua.com.foxminded.vehicles.service.ManufacturerService.MANUFACTURER_IS_NOT_PRESENT;
import static ua.com.foxminded.vehicles.service.ModelService.MODEL_IS_NOT_PRESENT;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entity.Vehicle;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.mapper.VehicleMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.repository.VehicleRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {
    
    public static final String VEHICLE_IS_NOT_PRESENT = "The vehicle with id=\"%s\" is not present";
    public static final String VEHICLE_IS_PRESENT = "The vehicle with id=\"%s\" has already present";
    
    private final VehicleRepository vehicleRepository;
    private final CategoryRepository categoryRepository;
    private final ModelRepository modelRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final VehicleMapper vehicleMapper;
    
    public Page<VehicleDto> getByCategory(String categoryName, Pageable pageable) {
            categoryRepository.findById(categoryName).orElseThrow(
                    () -> new ServiceException(String.format(CATEGORY_IS_NOT_PRESENT, categoryName), 
                                               BAD_REQUEST));
            return vehicleRepository.findByCategoriesName(categoryName, pageable)
                                    .map(vehicleMapper::map);
    }
    
    public Page<VehicleDto> getByModel(String modelName, Pageable pageable) {
            modelRepository.findById(modelName).orElseThrow(
                    () -> new ServiceException(String.format(MODEL_IS_NOT_PRESENT, modelName), 
                                               BAD_REQUEST));
            return vehicleRepository.findByModelName(modelName, pageable)
                                    .map(vehicleMapper::map);
    }
    
    public Page<VehicleDto> getByManufacturerNameAndMaxYear(String manufacturerName, 
                                                            int maxYear, 
                                                            Pageable pageable) {
            manufacturerRepository.findById(manufacturerName).orElseThrow(
                    () -> new ServiceException(String.format(MANUFACTURER_IS_NOT_PRESENT, manufacturerName), 
                                               BAD_REQUEST));
            return vehicleRepository.findByManufacturerNameAndProductionYearLessThanEqual(
                            manufacturerName, maxYear, pageable).map(vehicleMapper::map);
    }
    
    public Page<VehicleDto> getByManufacturerNameAndMinYear(String manufacturerName, 
                                                            int minYear, 
                                                            Pageable pageable) {
            manufacturerRepository.findById(manufacturerName).orElseThrow(
                    () -> new ServiceException(String.format(MANUFACTURER_IS_NOT_PRESENT, manufacturerName), 
                                               BAD_REQUEST));
            return vehicleRepository.findByManufacturerNameAndProductionYearGreaterThanEqual(
                    manufacturerName, minYear, pageable).map(vehicleMapper::map);
    }
    
    public VehicleDto save(VehicleDto vehicle) {
            var vehicleEntity = Vehicle.builder().productionYear(vehicle.getProductionYear()).build();
            
            if (vehicle.hasCategories()) {
                vehicleEntity.setCategories(new HashSet<>());
                
                for (CategoryDto category : vehicle.getCategories()) {
                    var categoryName = category.getName();
                    var categoryEntity = categoryRepository.findById(categoryName).orElseThrow(
                            () -> new ServiceException(String.format(CATEGORY_IS_NOT_PRESENT, categoryName), 
                                                       BAD_REQUEST));
                    vehicleEntity.addCategory(categoryEntity);
                }
            }
            
            if (vehicle.hasManufacturer()) {
                var manufacturerName = vehicle.getManufacturer().getName();
                var manufacturerEntity = manufacturerRepository.findById(manufacturerName)
                        .orElseThrow(() -> new ServiceException(String.format(MANUFACTURER_IS_NOT_PRESENT, 
                                                                              manufacturerName), 
                                                                BAD_REQUEST));
                vehicleEntity.setManufacturer(manufacturerEntity);
            } 
            
            if (vehicle.hasModel()) {
                var modelName = vehicle.getModel().getName();
                var modelEntity = modelRepository.findById(modelName).orElseThrow(
                        () -> new ServiceException(String.format(MODEL_IS_NOT_PRESENT, modelName), 
                                                                 BAD_REQUEST));
                vehicleEntity.setModel(modelEntity);
            }
            
            Vehicle persistedVehicle = vehicleRepository.saveAndFlush(vehicleEntity);
            return vehicleMapper.map(persistedVehicle);
    }
    
    public Page<VehicleDto> getAll(Pageable pageable) {
            return vehicleRepository.findAll(pageable).map(vehicleMapper::map);
    }
    
    public VehicleDto update(VehicleDto vehicle) {
            var vehicleEntity = vehicleRepository.findById(vehicle.getId()).orElseThrow(
                    () -> new ServiceException(String.format(VEHICLE_IS_NOT_PRESENT, vehicle.getId()), 
                                                             BAD_REQUEST));
            vehicleEntity.setProductionYear(vehicle.getProductionYear());
            updateManufacturer(vehicle, vehicleEntity);
            updateModel(vehicle, vehicleEntity);
            updateCategories(vehicle, vehicleEntity);
           
            Vehicle updatedVehicle = vehicleRepository.saveAndFlush(vehicleEntity);
            return vehicleMapper.map(updatedVehicle);
    }
    
    public void deleteById(String id) {
            vehicleRepository.findById(id).orElseThrow(
                    () -> new ServiceException(String.format(VEHICLE_IS_NOT_PRESENT, id), NO_CONTENT));
            vehicleRepository.deleteById(id);
    }
    
    public VehicleDto getById(String id) {
            Vehicle entity = vehicleRepository.findById(id).orElseThrow(
                    () -> new ServiceException(String.format(VEHICLE_IS_NOT_PRESENT, id), BAD_REQUEST));
            return vehicleMapper.map(entity);
    }
    
    private void updateCategories(VehicleDto vehicle, Vehicle vehicleEntity) {
        if (vehicle.getCategories() !=null && vehicleEntity.getCategories() != null) {
            List<Category> unnecessaryRelations = vehicleEntity.getCategories().stream()
                    .filter(categoryEntity -> {
                        return vehicle.getCategories().stream()
                                .noneMatch(category -> categoryEntity.getName().equals(category.getName()));
                    }).toList();

            unnecessaryRelations.stream().forEach(vehicleEntity::removeCategory);

            Set<CategoryDto> necessaryRelations = vehicle.getCategories().stream().filter(category -> {
                return vehicleEntity.getCategories().stream()
                        .noneMatch(categoryEntity -> category.getName().equals(categoryEntity.getName()));
            }).collect(Collectors.toSet());

            addCategories(necessaryRelations, vehicleEntity);
        } else if (vehicle.getCategories() == null && vehicleEntity.getCategories() != null) {
            Set<Category> unnecessaryRelations = vehicleEntity.getCategories();
            unnecessaryRelations.stream().forEach(vehicleEntity::removeCategory);
        } else if (vehicle.getCategories() != null) {
            Set<CategoryDto> necessaryRelations = vehicle.getCategories();
            addCategories(necessaryRelations, vehicleEntity);
        }
    }
    
    private void addCategories(Set<CategoryDto> categories, Vehicle vehicleEntity) {
        for (CategoryDto category : categories) {
            var categoryName = category.getName();
            var categoryEntity = categoryRepository.findById(categoryName)
                    .orElseThrow(() -> new ServiceException(String.format(CATEGORY_IS_NOT_PRESENT, 
                                                                          categoryName), 
                                                            BAD_REQUEST));
            vehicleEntity.addCategory(categoryEntity);
        }
    }
    
    private void updateModel(VehicleDto vehicle, Vehicle vehicleEntity) {
        if (vehicle.hasModel()) {
            var modelName = vehicle.getModel().getName();
            var modelEntity = modelRepository.findById(modelName)
                    .orElseThrow(() -> new ServiceException(String.format(MODEL_IS_NOT_PRESENT, modelName), 
                                                            BAD_REQUEST));
            vehicleEntity.setModel(modelEntity);
        } else {
            vehicleEntity.setModel(null);
        }
    }
    
    private void updateManufacturer(VehicleDto vehicle, Vehicle vehicleEntity) {
        if (vehicle.hasManufacturer()) {
            var manufacturerName = vehicle.getManufacturer().getName();
            var manufacturerEntity = manufacturerRepository.findById(manufacturerName)
                    .orElseThrow(() -> new ServiceException(String.format(MANUFACTURER_IS_NOT_PRESENT, 
                                                                          manufacturerName), 
                                                            BAD_REQUEST));
            vehicleEntity.setManufacturer(manufacturerEntity);
        } else {
            vehicleEntity.setManufacturer(null);
        }
    }
}
