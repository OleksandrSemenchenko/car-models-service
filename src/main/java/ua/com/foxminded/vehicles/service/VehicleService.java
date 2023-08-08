package ua.com.foxminded.vehicles.service;

import static ua.com.foxminded.vehicles.service.CategoryService.NO_CATEGORY;
import static ua.com.foxminded.vehicles.service.ManufacturerService.NO_MANUFACTURER;
import static ua.com.foxminded.vehicles.service.ModelService.NO_MODEL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entity.FilterParameter;
import ua.com.foxminded.vehicles.entity.Vehicle;
import ua.com.foxminded.vehicles.exception.CategoryNotFoundException;
import ua.com.foxminded.vehicles.exception.ManufacturerNotFoundException;
import ua.com.foxminded.vehicles.exception.ModelNotFoundException;
import ua.com.foxminded.vehicles.exception.VehicleNotFoundException;
import ua.com.foxminded.vehicles.mapper.VehicleMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.repository.VehicleRepository;
import ua.com.foxminded.vehicles.repository.VehicleSpecification;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {

    public static final String NO_VEHICLE = "The vehicle with id=\"%s\" doesn't exist";

    private final VehicleRepository vehicleRepository;
    private final CategoryRepository categoryRepository;
    private final ModelRepository modelRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final VehicleMapper vehicleMapper;
    
    public Page<VehicleDto> getAllByOptionalPredicates(FilterParameter parameter, Pageable pageable) {
        Specification<Vehicle> specification = VehicleSpecification.getSpecification(parameter);
        return vehicleRepository.findAll(specification, pageable).map(vehicleMapper::map);
     }

    public boolean existsById(String id) {
        return vehicleRepository.existsById(id);
    }

    public VehicleDto save(VehicleDto vehicleDto) {
        var vehicle = Vehicle.builder().productionYear(vehicleDto.getProductionYear()).build();
        
        updateCategoryRelations(vehicleDto, vehicle);
        updateManufacturerRelation(vehicleDto, vehicle);
        updateModelRelation(vehicleDto, vehicle);

        Vehicle persistedVehicle = vehicleRepository.saveAndFlush(vehicle);
        return vehicleMapper.map(persistedVehicle);
    }

    public VehicleDto update(VehicleDto vehicleDto) {
        var vehicle = vehicleRepository.findById(vehicleDto.getId()).orElseThrow(
                () -> new VehicleNotFoundException(String.format(NO_VEHICLE, vehicleDto.getId())));
        
        vehicle.setProductionYear(vehicleDto.getProductionYear());
        
        updateManufacturerRelation(vehicleDto, vehicle);
        updateModelRelation(vehicleDto, vehicle);
        updateCategoryRelations(vehicleDto, vehicle);

        Vehicle updatedVehicle = vehicleRepository.saveAndFlush(vehicle);
        return vehicleMapper.map(updatedVehicle);
    }

    public void deleteById(String id) {
        vehicleRepository.findById(id).orElseThrow(
                () -> new VehicleNotFoundException(String.format(NO_VEHICLE, id)));
        vehicleRepository.deleteById(id);
    }

    public VehicleDto getById(String id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(
                () -> new VehicleNotFoundException(String.format(NO_VEHICLE, id)));
        return vehicleMapper.map(vehicle);
    }

    private void updateCategoryRelations(VehicleDto vehicleDto, Vehicle vehicle) {
        if (vehicleDto.getCategories() != null && vehicle.getCategories() != null) {
            List<Category> unnecessaryRelations = vehicle.getCategories().stream().filter(category -> {
                return vehicleDto.getCategories().stream()
                        .noneMatch(categoryDto -> category.getName().equals(categoryDto.getName()));
            }).toList();

            unnecessaryRelations.stream().forEach(vehicle::removeCategory);

            Set<CategoryDto> necessaryRelations = vehicleDto.getCategories().stream().filter(categoryDto -> {
                return vehicle.getCategories().stream()
                        .noneMatch(category -> categoryDto.getName().equals(category.getName()));
            }).collect(Collectors.toSet());

            addCategoryRelations(necessaryRelations, vehicle);
        } else if (vehicleDto.getCategories() == null && vehicle.getCategories() != null) {
            Set<Category> unnecessaryRelations = vehicle.getCategories();
            unnecessaryRelations.stream().forEach(vehicle::removeCategory);
        } else if (vehicleDto.getCategories() != null) {
            Set<CategoryDto> necessaryRelations = vehicleDto.getCategories();
            vehicle.setCategories(new HashSet<>());
            addCategoryRelations(necessaryRelations, vehicle);
        }
    }

    private void addCategoryRelations(Set<CategoryDto> categoriesDto, Vehicle vehicle) {
        for (CategoryDto categoryDto : categoriesDto) {
            var categoryName = categoryDto.getName();
            var category = categoryRepository.findById(categoryName).orElseThrow(
                    () -> new CategoryNotFoundException(String.format(NO_CATEGORY, categoryName)));
            vehicle.addCategory(category);
        }
    }

    private void updateModelRelation(VehicleDto vehicleDto, Vehicle vehicle) {
        if (vehicleDto.hasModel()) {
            var modelName = vehicleDto.getModel().getName();
            var model = modelRepository.findById(modelName).orElseThrow(
                    () -> new ModelNotFoundException(String.format(NO_MODEL, modelName)));
            vehicle.setModel(model);
        } else {
            vehicle.setModel(null);
        }
    }

    private void updateManufacturerRelation(VehicleDto vehicleDto, Vehicle vehicle) {
        if (vehicleDto.hasManufacturer()) {
            var manufacturerName = vehicleDto.getManufacturer().getName();
            var manufacturer = manufacturerRepository.findById(manufacturerName).orElseThrow(
                    () -> new ManufacturerNotFoundException(String.format(NO_MANUFACTURER, manufacturerName)));
            vehicle.setManufacturer(manufacturer);
        } else {
            vehicle.setManufacturer(null);
        }
    }
}
