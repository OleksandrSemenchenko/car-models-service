package ua.com.foxminded.vehicles.service;

import static ua.com.foxminded.vehicles.service.CategoryService.NO_CATEGORY;
import static ua.com.foxminded.vehicles.service.ManufacturerService.NO_MANUFACTURER;
import static ua.com.foxminded.vehicles.service.ModelService.NO_MODEL;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import ua.com.foxminded.vehicles.entity.Vehicle;
import ua.com.foxminded.vehicles.exception.AlreadyExistsException;
import ua.com.foxminded.vehicles.exception.NotFoundException;
import ua.com.foxminded.vehicles.mapper.VehicleMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.repository.VehicleRepository;
import ua.com.foxminded.vehicles.specification.SearchFilter;
import ua.com.foxminded.vehicles.specification.VehicleSpecification;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {

    public static final String NO_SUCH_VEHICLE = "Such vehicle doesn't exist";
    public static final String NO_VEHICLE_ID = "The vehicle with id=%s doesn't exist";
    public static final String VEHICLE_ALREADY_EXISTS = "Such vehicle with id='%s' already exists";

    private final VehicleRepository vehicleRepository;
    private final CategoryRepository categoryRepository;
    private final ModelRepository modelRepository;
    private final ManufacturerRepository manufacturerRepository;
    private final VehicleMapper vehicleMapper;
    
    public Optional<VehicleDto> getByManufacturerAndModelAndYear(String manufacturer, String model, int year) {
        SearchFilter searchFilter = SearchFilter.builder().manufacturer(manufacturer).model(model).year(year).build();
        Specification<Vehicle> specification = VehicleSpecification.getSpecification(searchFilter);
        return vehicleRepository.findOne(specification).map(vehicleMapper::map);
    }
    
    public Page<VehicleDto> search(SearchFilter searchFilter, Pageable pageable) {
        Specification<Vehicle> specification = VehicleSpecification.getSpecification(searchFilter);
        return vehicleRepository.findAll(specification, pageable).map(vehicleMapper::map);
    }

    public VehicleDto save(VehicleDto vehicleDto) {
        throwIfPresentByManufacturerAndModelAndYear(vehicleDto.getManufacturer(), 
                                                    vehicleDto.getModel(), 
                                                    vehicleDto.getYear()); 
        
        var vehicle = Vehicle.builder().productionYear(vehicleDto.getYear())
                                       .categories(new HashSet<>())
                                       .build();
        
        updateCategoryRelations(vehicleDto, vehicle);
        updateManufacturerRelation(vehicleDto, vehicle);
        updateModelRelation(vehicleDto, vehicle);

        var persistedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.map(persistedVehicle);
    }

    public VehicleDto update(VehicleDto vehicleDto) {
        SearchFilter searchFilter = SearchFilter.builder().manufacturer(vehicleDto.getManufacturer())
                                                          .model(vehicleDto.getModel())
                                                          .year(vehicleDto.getYear())
                                                          .build();
        Specification<Vehicle> specification = VehicleSpecification.getSpecification(searchFilter);
        var vehicle = vehicleRepository.findOne(specification).orElseThrow(
                () -> new NotFoundException(NO_SUCH_VEHICLE));
        vehicle.setProductionYear(vehicleDto.getYear());
        
        updateManufacturerRelation(vehicleDto, vehicle);
        updateModelRelation(vehicleDto, vehicle);
        updateCategoryRelations(vehicleDto, vehicle);

        var updatedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.map(updatedVehicle);
    }

    public void deleteById(String id) {
        vehicleRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format(NO_VEHICLE_ID, id)));
        vehicleRepository.deleteById(id);
    }

    public Optional<VehicleDto> getById(String id) {
        return vehicleRepository.findById(id).map(vehicleMapper::map);
    }
    
    private void throwIfPresentByManufacturerAndModelAndYear(String manufacturer, String model, int year) {
        SearchFilter searchFilter = SearchFilter.builder().manufacturer(manufacturer).model(model).year(year).build();
        Specification<Vehicle> specification = VehicleSpecification.getSpecification(searchFilter);
        Optional<Vehicle> vehicleOptional = vehicleRepository.findOne(specification);
        
        if (vehicleOptional.isPresent()) {
            throw new AlreadyExistsException(String.format(VEHICLE_ALREADY_EXISTS, vehicleOptional.get().getId())); 
        }
    }

    private void updateCategoryRelations(VehicleDto vehicleDto, Vehicle vehicle) {
        List<Category> unnecessaryCategories = vehicle.getCategories().stream().filter(category -> {
            return vehicleDto.getCategories().stream()
                                             .noneMatch(categoryName -> category.getName().equals(categoryName));
        }).toList();

        for (Category category : unnecessaryCategories) {
            vehicle.removeCategory(category);
        }

        Set<CategoryDto> necessaryCategories = vehicleDto.getCategories().stream().filter(categoryDto -> {
            return vehicle.getCategories().stream()
                                          .noneMatch(category -> categoryDto.equals(category.getName()));
        }).map(categoryName -> CategoryDto.builder().name(categoryName).build())
          .collect(Collectors.toSet());

        addCategoryRelations(necessaryCategories, vehicle);
    }
    
    private void addCategoryRelations(Set<CategoryDto> categoriesDto, Vehicle vehicle) {
        for (CategoryDto categoryDto : categoriesDto) {
            var categoryName = categoryDto.getName();
            var category = categoryRepository.findById(categoryName).orElseThrow(
                    () -> new NotFoundException(String.format(NO_CATEGORY, categoryName)));
            vehicle.addCategory(category);
        }
    }

    private void updateModelRelation(VehicleDto vehicleDto, Vehicle vehicle) {
        if (vehicleDto.getModel() != null) {
            var modelName = vehicleDto.getModel();
            var model = modelRepository.findById(modelName).orElseThrow(
                    () -> new NotFoundException(String.format(NO_MODEL, modelName)));
            vehicle.setModel(model);
        } else {
            vehicle.setModel(null);
        }
    }

    private void updateManufacturerRelation(VehicleDto vehicleDto, Vehicle vehicle) {
        if (vehicleDto.getManufacturer() != null) {
            var manufacturerName = vehicleDto.getManufacturer();
            var manufacturer = manufacturerRepository.findById(manufacturerName).orElseThrow(
                    () -> new NotFoundException(String.format(NO_MANUFACTURER, manufacturerName)));
            vehicle.setManufacturer(manufacturer);
        } else {
            vehicle.setManufacturer(null);
        }
    }
}
