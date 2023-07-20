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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;

import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.entity.Vehicle;
import ua.com.foxminded.vehicles.entitymother.CategoryMother;
import ua.com.foxminded.vehicles.entitymother.ManufacturerMother;
import ua.com.foxminded.vehicles.entitymother.ModelMother;
import ua.com.foxminded.vehicles.entitymother.VehicleMother;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class VehicleRepositoryTest {
    
    public static final int PRODUCTION_YEAR = 2022;

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
        manufacturer = ManufacturerMother.complete().build();
        manufacturerRepository.saveAndFlush(manufacturer);
        
        category = CategoryMother.complete().build();
        categoryRepository.saveAndFlush(category);
        
        model = ModelMother.complete().build();
        modelRepository.saveAndFlush(model);
        
        firstVehicle = VehicleMother.complete()
                                          .manufacturer(manufacturer)
                                          .model(model).build();
        firstVehicle.setCategories(new HashSet<>());
        category.setVehicles(new HashSet<Vehicle>());
        firstVehicle.addCategory(category);
        
        secondVehicle = Vehicle.builder().productionYear(PRODUCTION_YEAR)
                                              .manufacturer(manufacturer).build();
        vehicleRepository.saveAndFlush(firstVehicle);
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
