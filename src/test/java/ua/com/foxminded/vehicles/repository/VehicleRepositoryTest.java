package ua.com.foxminded.vehicles.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.transaction.BeforeTransaction;

import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.entity.Vehicle;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class VehicleRepositoryTest {
    
    public static final String MANUFACTURER_NAME = "Audi";
    public static final String CATEGORY_NAME = "SUV";
    public static final String MODEL_NAME = "Q8";
    public static final int SECOND_VEHICLE_PRODUCTION_YEAR = 2022;
    public static final int FIRST_VEHICLE_PRODUCTION_YEAR = 2023;

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ModelRepository modelRepository;
    
    private Model model;
    private Category category;
    private Manufacturer manufacturer;
    private Vehicle firstVehicle;
    private  Vehicle secondVehicle;
    private Pageable pageable;
    
    @BeforeTransaction
    void init() {
        manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        manufacturerRepository.saveAndFlush(manufacturer);
        
        category = Category.builder().name(CATEGORY_NAME).build();
        categoryRepository.saveAndFlush(category);
        category.setVehicles(new HashSet<Vehicle>());
        
        model = Model.builder().name(MODEL_NAME).build();
        modelRepository.saveAndFlush(model);
        
        firstVehicle = Vehicle.builder().productionYear(FIRST_VEHICLE_PRODUCTION_YEAR)
                                        .manufacturer(manufacturer)
                                        .model(model)
                                        .categories(new HashSet<>())
                                        .build();
        firstVehicle.addCategory(category);
        vehicleRepository.saveAndFlush(firstVehicle);
        
        secondVehicle = Vehicle.builder().productionYear(SECOND_VEHICLE_PRODUCTION_YEAR)
                                         .manufacturer(manufacturer)
                                         .build();
        vehicleRepository.saveAndFlush(secondVehicle);
    }
    
    @BeforeEach
    void setUp() {
        pageable = PageRequest.ofSize(100);
    }
    
    void findByModelName_ShouldReturnVehiclesPage() {
        Page<Vehicle> vehicles = vehicleRepository.findByModelName(model.getName(), pageable);
        
        assertEquals(model, vehicles.getContent().iterator().next().getModel());
    }
    
    @Test
    void findByCategoriesName_ShouldReturnVehiclesPage() {
        Page<Vehicle> vehicles = vehicleRepository.findByCategoriesName(category.getName(), pageable);
        
        assertEquals(category, vehicles.getContent().iterator().next().getCategories().iterator().next());
    }
    
    @Test
    void findByManufacturerNameAndProductionYearLessThanEqual_ShouldReturnVehiclesPage() {
        Page<Vehicle> vehiclesPage = vehicleRepository.findByManufacturerNameAndProductionYearLessThanEqual(
                manufacturer.getName(), secondVehicle.getProductionYear(), pageable);
        
        assertEquals(secondVehicle, vehiclesPage.getContent().iterator().next());
    }
    
    @Test
    void findByManufacturerNameAndProductionYearGreaterThan_ShouldReturnVehiclesPage() {
        Page<Vehicle> vehiclesPage = vehicleRepository
                .findByManufacturerNameAndProductionYearGreaterThanEqual(
                        manufacturer.getName(), firstVehicle.getProductionYear(), pageable);
        
        assertEquals(firstVehicle, vehiclesPage.getContent().iterator().next());
    }
}
