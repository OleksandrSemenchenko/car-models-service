package ua.com.foxminded.vehicles.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ua.com.foxminded.vehicles.service.CategoryServiceTest.CATEGORY_NAME;
import static ua.com.foxminded.vehicles.service.ManufacturerServiceTest.MANUFACTURER_NAME;
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
import ua.com.foxminded.vehicles.exception.AlreadyExistsException;
import ua.com.foxminded.vehicles.exception.NotFoundException;
import ua.com.foxminded.vehicles.mapper.VehicleMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.repository.VehicleRepository;
import ua.com.foxminded.vehicles.specification.SearchFilter;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {
    
    public static final String VEHICLE_ID = "1";
    public static final int PRODUCTION_YEAR = 2020;
    
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
    
    private Manufacturer manufacturer;
    private Model model;
    private Category category;
    private Vehicle vehicle;
    private VehicleDto vehicleDto;
    
    @BeforeEach
    void SetUp() {
        manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        model = Model.builder().name(MODEL_NAME).build();
        category = Category.builder().name(CATEGORY_NAME).build();
        vehicle = Vehicle.builder().id(VEHICLE_ID)
                                   .productionYear(PRODUCTION_YEAR)
                                   .manufacturer(manufacturer)
                                   .model(model)
                                   .categories(new HashSet<>(Arrays.asList(category)))
                                   .build();
        category.setVehicles(new HashSet<>(Arrays.asList(vehicle)));
        
        vehicleDto = VehicleDto.builder().id(VEHICLE_ID)
                                         .year(PRODUCTION_YEAR)
                                         .manufacturer(MANUFACTURER_NAME)
                                         .model(MODEL_NAME)
                                         .categories(new HashSet<String>(Arrays.asList(CATEGORY_NAME)))
                                         .build();
    }
    
    @Test
    void getByManufacturerAndModelAndYear_ShouldGetVehicles() {
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.of(vehicle));
        when(vehicleMapper.map(vehicle)).thenReturn(vehicleDto);
        vehicleService.getByManufacturerAndModelAndYear(MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR);
        
        verify(vehicleRepository).findOne(ArgumentMatchers.<Specification<Vehicle>>any());
    }
    
    @Test
    void search_SholdSeachVehicles() {
        SearchFilter seachFilter = new SearchFilter();
        Pageable pageable = Pageable.unpaged();
        Page<Vehicle> vehiclesPage = new PageImpl<Vehicle>(Arrays.asList(vehicle));
        when(vehicleRepository.findAll(ArgumentMatchers.<Specification<Vehicle>>any(), isA(Pageable.class)))
            .thenReturn(vehiclesPage);
        when(vehicleMapper.map(vehicle)).thenReturn(vehicleDto);
        vehicleService.search(seachFilter, pageable);
        
        verify(vehicleRepository).findAll(ArgumentMatchers.<Specification<Vehicle>>any(), isA(Pageable.class));
        verify(vehicleMapper).map(vehicle);
    }
    
    @Test
    void save_ShouldSaveVehicle() {
        vehicle.setId(null);
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(vehicleDto.getCategories()
                                                   .iterator()
                                                   .next())).thenReturn(Optional.of(category));
        when(manufacturerRepository.findById(vehicleDto.getManufacturer())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(vehicleDto.getModel())).thenReturn(Optional.of(model));
        when(vehicleRepository.save(vehicle)).thenReturn(vehicle);
        when(vehicleMapper.map(vehicle)).thenReturn(vehicleDto);
        vehicleService.save(vehicleDto);

        verify(vehicleRepository).findOne(ArgumentMatchers.<Specification<Vehicle>>any());
        verify(categoryRepository).findById(vehicleDto.getCategories().iterator().next());
        verify(modelRepository).findById(vehicleDto.getModel());
        verify(vehicleRepository).save(vehicle);
        verify(vehicleMapper).map(vehicle);
    }
    
    @Test
    void save_ShouldThrow_WhenNoSuchModel() {
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(vehicleDto.getCategories()
                                                   .iterator()
                                                   .next())).thenReturn(Optional.of(category));
        when(manufacturerRepository.findById(vehicleDto.getManufacturer())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(vehicleDto.getModel())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehicleService.save(vehicleDto));
    }
    
    @Test
    void save_ShouldThrow_WhenNoSuchManufacturer() {
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(vehicleDto.getCategories()
                                                   .iterator()
                                                   .next())).thenReturn(Optional.of(category));
        when(manufacturerRepository.findById(vehicleDto.getManufacturer())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> vehicleService.save(vehicleDto));
    }
    
    @Test
    void save_ShouldThrow_WhenNoSuchCategory() {
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.empty());
        when(categoryRepository.findById(vehicleDto.getCategories().iterator().next())).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> vehicleService.save(vehicleDto));
    }
    
    @Test
    void save_ShouldThrow_WhenVehicleAlreadyExists() {
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.of(vehicle));
        assertThrows(AlreadyExistsException.class, () -> vehicleService.save(vehicleDto));
    }
    
    @Test
    void update_ShouldThrow_WhenNoCategory() {
        String updatedCategoryName = "Pickup";
        vehicleDto.setCategories(Set.of(updatedCategoryName));
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.of(vehicle));
        when(manufacturerRepository.findById(vehicleDto.getManufacturer())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(vehicleDto.getModel())).thenReturn(Optional.of(model));
        when(categoryRepository.findById(vehicleDto.getCategories().iterator().next())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, 
                () -> vehicleService.update(MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR, vehicleDto));
    }
    
    @Test
    void update_ShouldThrow_WhenNoModel() {
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.of(vehicle));
        when(manufacturerRepository.findById(vehicleDto.getManufacturer())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(vehicleDto.getModel())).thenReturn(Optional.empty());
    
        assertThrows(NotFoundException.class, 
            () -> vehicleService.update(MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR, vehicleDto));
    }
    
    @Test
    void update_ShouldThrow_WhenNoManufacturer() {
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
                .thenReturn(Optional.of(vehicle));
        when(manufacturerRepository.findById(vehicleDto.getManufacturer())).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, 
                () -> vehicleService.update(MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR, vehicleDto));
    }
    
    @Test
    void update_ShouldThrow_WhenNoSuchVehicle() {
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, 
                () -> vehicleService.update(MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR, vehicleDto));
    }
    
    
    @Test
    void update_ShouldUpdateVehicle() {
        String updatedCategoryName = "Pickup";
        Category updatedCategory = Category.builder().name(updatedCategoryName)
                                                     .vehicles(new HashSet<>())
                                                     .build();
        vehicleDto.setCategories(Set.of(updatedCategoryName));
        when(vehicleRepository.findOne(ArgumentMatchers.<Specification<Vehicle>>any()))
            .thenReturn(Optional.of(vehicle));
        when(manufacturerRepository.findById(vehicleDto.getManufacturer())).thenReturn(Optional.of(manufacturer));
        when(modelRepository.findById(vehicleDto.getModel())).thenReturn(Optional.of(model));
        when(categoryRepository.findById(vehicleDto.getCategories()
                                                   .iterator()
                                                   .next())).thenReturn(Optional.of(updatedCategory));
        Vehicle updatedVehicle = Vehicle.builder().id(vehicle.getId())
                                                  .productionYear(vehicleDto.getYear())
                                                  .manufacturer(manufacturer)
                                                  .model(model)
                                                  .categories(new HashSet<Category>(Arrays.asList(updatedCategory)))
                                                  .build();
        when(vehicleRepository.save(updatedVehicle)).thenReturn(updatedVehicle);
        when(vehicleMapper.map(updatedVehicle)).thenReturn(vehicleDto);
        
        vehicleService.update(MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR, vehicleDto);
        
        verify(vehicleRepository).findOne(ArgumentMatchers.<Specification<Vehicle>>any());
        verify(manufacturerRepository).findById(vehicleDto.getManufacturer());
        verify(modelRepository).findById(vehicleDto.getModel());
        verify(categoryRepository).findById(vehicleDto.getCategories().iterator().next());
        verify(vehicleRepository).save(updatedVehicle);
        verify(vehicleMapper).map(updatedVehicle);
    }
    
    @Test
    void deleteById_ShouldThrow_WhenNoVehicle() {
        String notExistingVehicleId = "10";
        when(vehicleRepository.findById(notExistingVehicleId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> vehicleService.deleteById(notExistingVehicleId));
    }
    
    @Test
    void deleteById_ShouldDeleteVehicle() {
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));
        vehicleService.deleteById(VEHICLE_ID);
        
        verify(vehicleRepository).findById(VEHICLE_ID);
        verify(vehicleRepository).deleteById(VEHICLE_ID);
    }

    @Test
    void getById_ShouldGetVehicle() {
        when(vehicleRepository.findById(VEHICLE_ID)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.map(vehicle)).thenReturn(vehicleDto);
        vehicleService.getById(VEHICLE_ID);
        
        verify(vehicleRepository).findById(VEHICLE_ID);
        verify(vehicleMapper).map(vehicle);
    }
}
