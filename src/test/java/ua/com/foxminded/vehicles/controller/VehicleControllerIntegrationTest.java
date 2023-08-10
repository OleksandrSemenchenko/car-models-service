package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.vehicles.controller.CategoryControllerIntegrationTest.CATEGORY_NAME;
import static ua.com.foxminded.vehicles.controller.ManufacturerControllerIntegrationTest.MANUFACTURER_NAME;
import static ua.com.foxminded.vehicles.controller.ModelControllerIntegrationTest.MODEL_NAME;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.entity.Vehicle;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.repository.VehicleRepository;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Transactional
class VehicleControllerIntegrationTest {
    
    public static final int PRODUCTION_YEAR = 2020;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    
    @Autowired
    private ModelRepository modelRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private Vehicle vehicle;
    private Vehicle youngerVehicle;
    private Model model;
    private Manufacturer manufacturer;
    private Category category;
    private ObjectMapper mapper;
    
    @BeforeTransaction
    void init() {
        mapper = new ObjectMapper();

        category = Category.builder().name(CATEGORY_NAME).build();
        categoryRepository.saveAndFlush(category);
        
        model = Model.builder().name(MODEL_NAME).build();
        modelRepository.saveAndFlush(model);
        
        manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        manufacturerRepository.saveAndFlush(manufacturer);

        vehicle = Vehicle.builder().productionYear(PRODUCTION_YEAR)
                                   .manufacturer(manufacturer)
                                   .categories(new HashSet<>())
                                   .model(model).build();
        category.setVehicles(new HashSet<>());
        vehicle.addCategory(category);
        int moreRecentProductionYear = 2023;
        youngerVehicle = Vehicle.builder().productionYear(moreRecentProductionYear)
                                                .manufacturer(manufacturer).build();
        vehicleRepository.saveAndFlush(vehicle);
        vehicleRepository.saveAndFlush(youngerVehicle);
    }
    
    @Test
    void search_ShouldReturnStatus404_WhenNotExistingParameters() throws Exception {
        String notExistingModel = "Insight";
        String notExistingCategory = "Coupe";
        String notExistingManufacturer = "Peugeot";
        
        mockMvc.perform(get("/v1/vehicles").param("model", notExistingModel)
                                           .param("category", notExistingCategory)
                                           .param("manufacturer", notExistingManufacturer)
                                           .param("maxYear", String.valueOf(vehicle.getProductionYear()))
                                           .param("minYear", String.valueOf(vehicle.getProductionYear())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(0)));
    }
    
    @Test
    void search_ShouldReturnStatusIsOk_WhenParametersArePresent() throws Exception {
        mockMvc.perform(get("/v1/vehicles").param("model", model.getName())
                                           .param("category", category.getName())
                                           .param("manufacturer", manufacturer.getName())
                                           .param("maxYear", String.valueOf(vehicle.getProductionYear()))
                                           .param("minYear", String.valueOf(vehicle.getProductionYear())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(1)))
               .andExpect(jsonPath("$.content[0].productionYear", is(vehicle.getProductionYear())))
               .andExpect(jsonPath("$.content[0].manufacturer.name", is(manufacturer.getName())))
               .andExpect(jsonPath("$.content[0].categories[0].name", is(category.getName())));
    }
    
    @Test
    void search_ShouldReturnStatusIsOk_WhenNoParameters() throws Exception {
        mockMvc.perform(get("/v1/vehicles"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(2)));
    }
    
    @Test
    void getById_ShouldReturnStatus404_WhenNoVehicle() throws Exception {
        String notExistingId = "1";
        
        mockMvc.perform(get("/v1/vehicles/{id}", notExistingId))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void getById_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/vehicles/{id}", vehicle.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.productionYear").value(vehicle.getProductionYear()));
    }
    
    @Test
    void save_ShouldReturnStatus404_WhenNoCategory() throws Exception {
        String notExistingCategoryName = "Pickup";
        CategoryDto categoryDto = CategoryDto.builder().name(notExistingCategoryName).build();
        VehicleDto vehicleDto = VehicleDto.builder().categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             manufacturer.getName(), 
                             model.getName(), 
                             String.valueOf(PRODUCTION_YEAR)).contentType(APPLICATION_JSON)
                                                             .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void save_ShouldReturnStatus404_WhenNoModel() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(category.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingModelName = "Mustang";
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             manufacturer.getName(), 
                             notExistingModelName, 
                             String.valueOf(PRODUCTION_YEAR)).contentType(APPLICATION_JSON)
                                                             .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void save_ShouldReturnStatus404_WhenNoManufacturer() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(category.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingManufacturerName = "Volvo";
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             notExistingManufacturerName, 
                             model.getName(), 
                             String.valueOf(PRODUCTION_YEAR)).contentType(APPLICATION_JSON)
                                                             .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void save_ShouldReturnStatus201() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(category.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             manufacturer.getName(), 
                             model.getName(), 
                             String.valueOf(PRODUCTION_YEAR))
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/vehicles/")));
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoCategory() throws Exception {
        String notExistingCategoryName = "Coupe";
        CategoryDto categoryDto = CategoryDto.builder().name(notExistingCategoryName).build();
        VehicleDto vehicleDto = VehicleDto.builder().id(vehicle.getId())
                                                    .categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturer.getName(), 
                            model.getName(), 
                            PRODUCTION_YEAR).contentType(APPLICATION_JSON)
                                            .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoModel() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(category.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().id(vehicle.getId())
                                                    .categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingModelName = "Pickup";
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturer.getName(), 
                            notExistingModelName, 
                            PRODUCTION_YEAR).contentType(APPLICATION_JSON)
                                            .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoManufacturer() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(category.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().id(vehicle.getId())
                                                    .categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingManufacturerName = "Ford";
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            notExistingManufacturerName, 
                            model.getName(), 
                            PRODUCTION_YEAR).contentType(APPLICATION_JSON)
                                            .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoVehicle() throws Exception {
        String notExistingId = "1";
        VehicleDto vehicleDto = VehicleDto.builder().id(notExistingId).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturer.getName(), 
                            model.getName(), 
                            PRODUCTION_YEAR).contentType(APPLICATION_JSON)
                                            .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus400_WhenMethodArgumentNotValid() throws Exception {
        VehicleDto vehicleDto = VehicleDto.builder().build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturer.getName(), 
                            model.getName(), 
                            PRODUCTION_YEAR).contentType(APPLICATION_JSON)
                                            .content(vehicleDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void update_ShouldReturnStatusIsOk() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(category.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().id(vehicle.getId()).categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturer.getName(), 
                            model.getName(), 
                            PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isOk());
        
        Vehicle persistedVehicle = vehicleRepository.findById(vehicle.getId()).orElseThrow();
        
        assertEquals(categoryDto.getName(), persistedVehicle.getCategories().iterator().next().getName());
        assertEquals(manufacturer.getName(), persistedVehicle.getManufacturer().getName());
        assertEquals(model.getName(), persistedVehicle.getModel().getName());
        assertEquals(PRODUCTION_YEAR, persistedVehicle.getProductionYear());
    }
    
    @Test
    void deleteById_ShouldReturnStatus404_WhenNoVehicle() throws Exception {
        String notExistingId = "1";
        mockMvc.perform(delete("/v1/vehicles/{id}", notExistingId))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteById_ShouldReturnStatusIs204() throws Exception {
        mockMvc.perform(delete("/v1/vehicles/{id}", vehicle.getId()))
               .andExpect(status().isNoContent());
        
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicle.getId());
        
        assertTrue(vehicleOptional.isEmpty());
    }
}
