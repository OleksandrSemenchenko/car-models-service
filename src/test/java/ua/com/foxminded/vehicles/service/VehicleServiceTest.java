package ua.com.foxminded.vehicles.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.com.foxminded.vehicles.service.CategoryServiceTest.CATEGORY_NAME;
import static ua.com.foxminded.vehicles.service.ManufacturerServiceTestTest.MANUFACTURER_NAME;
import static ua.com.foxminded.vehicles.service.ModelServiceTest.MODEL_NAME;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.entity.Vehicle;
import ua.com.foxminded.vehicles.mapper.VehicleMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.repository.VehicleRepository;
import ua.com.foxminded.vehicles.specification.SearchFilter;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {
    
    public static final String VEHICLE_ID = "1";
    
    @InjectMocks
    private VehicleService vehicleService;
    
    @Mock
    private VehicleRepository vehicleRepository;
    
    @Mock 
    private ManufacturerRepository manufacturerRepository;
    
    @Mock
    private ModelRepository modelRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private VehicleMapper vehicleMapper;
    
    private Vehicle vehicle;
    
    @BeforeEach
    void SetUp() {
        vehicle = Vehicle.builder().id(VEHICLE_ID).build();
    }
    
    @Test
    void search_ShouldPerformCorrectCalls() {
        SearchFilter seachFilter = new SearchFilter();
        Pageable pageable = Pageable.unpaged();
        Page<Vehicle> vehiclesPage = new PageImpl<Vehicle>(Arrays.asList(vehicle));
        when(vehicleRepository.findAll(ArgumentMatchers.<Specification<Vehicle>>any(), isA(Pageable.class)))
                .thenReturn(vehiclesPage);
        vehicleService.search(seachFilter, pageable);
        verify(vehicleRepository).findAll(ArgumentMatchers.<Specification<Vehicle>>any(), isA(Pageable.class));
        verify(vehicleMapper).map(isA(Vehicle.class));
    }
    
    @Test
    void save_ShouldPerformCorrectCalls() {
        Manufacturer manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        Model model = Model.builder().name(MODEL_NAME).build();
        Category category = Category.builder().name(CATEGORY_NAME)
                                              .vehicles(new HashSet<Vehicle>())
                                              .build();
        vehicle.setId(null);
        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
        when(manufacturerRepository.findById(anyString())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(anyString())).thenReturn(Optional.of(model));
        when(vehicleRepository.save(isA(Vehicle.class))).thenReturn(vehicle);
        VehicleDto vehicleDto = VehicleDto.builder().id(VEHICLE_ID)
                                                    .manufacturer(MANUFACTURER_NAME)
                                                    .model(MODEL_NAME)
                                                    .categories(Set.of(CATEGORY_NAME))
                                                    .build();
        vehicleService.save(vehicleDto);
        
        verify(manufacturerRepository).findById(anyString());
        verify(modelRepository).findById(anyString());
        verify(categoryRepository).findById(anyString());
        verify(vehicleRepository).save(isA(Vehicle.class));
        verify(vehicleMapper).map(isA(Vehicle.class));
        
        
    }
    
    @Test
    void update_ShouldPerformCorrectCalls_WhenVehicleDtoHasAndVehicleHasNoCategories() {
        Manufacturer manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        Model model = Model.builder().name(MODEL_NAME).build();
        Category category = Category.builder().name(CATEGORY_NAME)
                                              .vehicles(new HashSet<Vehicle>())
                                              .build();
        when(vehicleRepository.findById(anyString())).thenReturn(Optional.of(vehicle));
        when(manufacturerRepository.findById(anyString())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(anyString())).thenReturn(Optional.of(model));
        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
        when(vehicleRepository.save(isA(Vehicle.class))).thenReturn(vehicle);
        VehicleDto vehicleDto = VehicleDto.builder().id(VEHICLE_ID)
                                                    .manufacturer(MANUFACTURER_NAME)
                                                    .model(MODEL_NAME)
                                                    .categories(Set.of(CATEGORY_NAME))
                                                    .build();
        vehicleService.update(vehicleDto);
        
        verify(vehicleRepository).findById(anyString());
        verify(manufacturerRepository).findById(anyString());
        verify(modelRepository).findById(anyString());
        verify(categoryRepository).findById(anyString());
        verify(vehicleRepository).save(isA(Vehicle.class));
        verify(vehicleMapper).map(isA(Vehicle.class));
    }
    
    @Test
    void update_ShouldPerformCorrectCalls_WhenVehicleDtoHasNoCategories() {
        Manufacturer manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        Model model = Model.builder().name(MODEL_NAME).build();
        Category category = Category.builder().name(CATEGORY_NAME).build();
        vehicle.setCategories(new HashSet <Category>(Arrays.asList(category)));
        category.setVehicles(new HashSet<Vehicle>(Arrays.asList(vehicle)));
        when(vehicleRepository.findById(anyString())).thenReturn(Optional.of(vehicle));
        when(manufacturerRepository.findById(anyString())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(anyString())).thenReturn(Optional.of(model));
        when(vehicleRepository.save(isA(Vehicle.class))).thenReturn(vehicle);
        VehicleDto vehicleDto = VehicleDto.builder().id(VEHICLE_ID)
                                                    .manufacturer(MANUFACTURER_NAME)
                                                    .model(MODEL_NAME)
                                                    .build();
        vehicleService.update(vehicleDto);
        
        verify(vehicleRepository).findById(anyString());
        verify(manufacturerRepository).findById(anyString());
        verify(modelRepository).findById(anyString());
        verify(vehicleRepository).save(isA(Vehicle.class));
        verify(vehicleMapper).map(isA(Vehicle.class));
    }
    
    @Test
    void update_ShouldPerformCorrectCalls_WhenVehicleDtoAndVehicleHasCategories() {
        Manufacturer manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        Model model = Model.builder().name(MODEL_NAME).build();
        Category category = Category.builder().name(CATEGORY_NAME).build();
        vehicle.setCategories(new HashSet<Category>(Arrays.asList(category)));
        category.setVehicles(new HashSet<Vehicle>(Arrays.asList(vehicle)));
        when(vehicleRepository.findById(anyString())).thenReturn(Optional.of(vehicle));
        when(manufacturerRepository.findById(anyString())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(anyString())).thenReturn(Optional.of(model));
        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
        when(vehicleRepository.save(isA(Vehicle.class))).thenReturn(vehicle);
        String newCategoryName = "Pickup";
        VehicleDto vehicleDto = VehicleDto.builder().id(VEHICLE_ID)
                                                    .manufacturer(MANUFACTURER_NAME)
                                                    .model(MODEL_NAME)
                                                    .categories(Set.of(newCategoryName))
                                                    .build();
        vehicleService.update(vehicleDto);
        
        verify(vehicleRepository).findById(anyString());
        verify(manufacturerRepository).findById(anyString());
        verify(modelRepository).findById(anyString());
        verify(categoryRepository).findById(anyString());
        verify(vehicleRepository).save(isA(Vehicle.class));
        verify(vehicleMapper).map(isA(Vehicle.class));
    }
    
    @Test
    void deleteById_ShouldPerformCorrectCalls() {
        when(vehicleRepository.findById(anyString())).thenReturn(Optional.of(vehicle));
        vehicleService.deleteById(VEHICLE_ID);
        
        verify(vehicleRepository).findById(anyString());
        verify(vehicleRepository).deleteById(anyString());
    }

    @Test
    void getById_ShouldPerformCorrectCalls() {
        when(vehicleRepository.findById(anyString())).thenReturn(Optional.of(vehicle));
        vehicleService.getById(anyString());
        
        verify(vehicleRepository).findById(anyString());
        verify(vehicleMapper).map(isA(Vehicle.class));
    }

}
