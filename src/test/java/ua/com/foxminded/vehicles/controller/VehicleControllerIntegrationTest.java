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
import static ua.com.foxminded.vehicles.controller.VehicleController.MAX_PRODUCTION_YEAR;
import static ua.com.foxminded.vehicles.controller.VehicleController.MIN_PRODUCTION_YEAR;

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
    
    private Vehicle vehicleEntity;
    private Vehicle youngerVehicleEntity;
    private Model modelEntity;
    private Manufacturer manufacturerEntity;
    private Category categoryEntity;
    private ObjectMapper mapper;
    
    @BeforeTransaction
    void init() {
        mapper = new ObjectMapper();

        categoryEntity = Category.builder().name(CATEGORY_NAME).build();
        categoryRepository.saveAndFlush(categoryEntity);
        
        modelEntity = Model.builder().name(MODEL_NAME).build();
        modelRepository.saveAndFlush(modelEntity);
        
        manufacturerEntity = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        manufacturerRepository.saveAndFlush(manufacturerEntity);

        vehicleEntity = Vehicle.builder().productionYear(PRODUCTION_YEAR)
                                         .manufacturer(manufacturerEntity)
                                         .categories(new HashSet<>())
                                         .model(modelEntity).build();
        categoryEntity.setVehicles(new HashSet<>());
        vehicleEntity.addCategory(categoryEntity);
        int earlierProductionYear = 2023;
        youngerVehicleEntity = Vehicle.builder().productionYear(earlierProductionYear)
                                                .manufacturer(manufacturerEntity).build();
        vehicleRepository.saveAndFlush(vehicleEntity);
        vehicleRepository.saveAndFlush(youngerVehicleEntity);
    }
    
    @Test
    void getByModel_ShouldReturnStatus400_WhenModelDoesNotExist() throws Exception {
        String notExistingModelName = "EcoSport";
        
        mockMvc.perform(get("/v1/models/{model}/vehicles", notExistingModelName))
               .andExpect(status().is(400));
    }
    
    @Test
    void getByModel_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/models/{model}/vehicles", modelEntity.getName()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].id", is(vehicleEntity.getId())));
    }
    
    @Test
    void getByCategory_ShouldReturnStatus400_WhenCategoryDoesNotExist() throws Exception {
        String notExistingCategoryName = "SUV";
        mockMvc.perform(get("/v1/categories/{category}/vehicles", notExistingCategoryName))
               .andExpect(status().is(400));
    }
    
    @Test
    void getByCategory_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/categories/{category}/vehicles", categoryEntity.getName()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].id", is(vehicleEntity.getId())));
    }
    
    @Test
    void getByManufacturerAndMaxProductionYear_ShouldREturnStatus400_WhenManufacturerDoesNotExist() 
            throws Exception {
        String notExistingManufacturerName = "Reno";
        String maxYear = String.valueOf(vehicleEntity.getProductionYear());

        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/vehicles", notExistingManufacturerName)
                    .param(MAX_PRODUCTION_YEAR, maxYear))
               .andExpect(status().is(400));
    }
    
    @Test
    void getByManufacturerAndMaxProductionYear_ShouldReturnStatusIsOk() throws Exception {
        String maxYear = String.valueOf(vehicleEntity.getProductionYear());

        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/vehicles", manufacturerEntity.getName())
                    .param(MAX_PRODUCTION_YEAR, maxYear))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(1)))
               .andExpect(jsonPath("$.content[0].id", is(vehicleEntity.getId())));
    }
    
    @Test
    void getByManufacturerNameAndMinProductionYear_ShouldReturnStatus400_WhenManufacturerDoesNotExist() 
            throws Exception {
        String notExistingManufacturerName = "Reno";
        String minYear = String.valueOf(youngerVehicleEntity.getProductionYear());

        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/vehicles", notExistingManufacturerName)
               .param(MIN_PRODUCTION_YEAR, minYear))
               .andExpect(status().is(400));
    }
    
    @Test
    void getByManufacturerNameAndMinYear_ShouldReturnStatusIsOk() throws Exception {
        String minYear = String.valueOf(youngerVehicleEntity.getProductionYear());
        
        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/vehicles", manufacturerEntity.getName())
                    .param(MIN_PRODUCTION_YEAR, minYear))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(1)))
               .andExpect(jsonPath("$.content[0].id", is(youngerVehicleEntity.getId())));
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenCategoryDoesNotExist() throws Exception {
        String notExistingCategoryName = "Pickup";
        CategoryDto categoryDto = CategoryDto.builder().name(notExistingCategoryName).build();
        VehicleDto vehicleDto = VehicleDto.builder().categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             manufacturerEntity.getName(), 
                             modelEntity.getName(), 
                             String.valueOf(PRODUCTION_YEAR))
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().is(400));
    }
    
    
    @Test
    void save_ShouldReturnStatus400_WhenModelDoesNotExist() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(categoryEntity.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingModelName = "Mustang";
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             manufacturerEntity.getName(), 
                             notExistingModelName, 
                             String.valueOf(PRODUCTION_YEAR))
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenManufacturerDoesNotExist() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(categoryEntity.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingManufacturerName = "Volvo";
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             notExistingManufacturerName, 
                             modelEntity.getName(), 
                             String.valueOf(PRODUCTION_YEAR))
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatusIsOk() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().name(categoryEntity.getName()).build();
        VehicleDto vehicleDto = VehicleDto.builder().categories(Set.of(categoryDto)).build();
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             manufacturerEntity.getName(), 
                             modelEntity.getName(), 
                             String.valueOf(PRODUCTION_YEAR))
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/vehicles/")));
    }
    
    @Test
    void getAll_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/vehicles"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(2)));
    }
    
    @Test
    void getById_ShouldReturnStatus400() throws Exception {
        String notExistingId = "1";
        
        mockMvc.perform(get("/v1/vehicles/{id}", notExistingId))
               .andExpect(status().is(400));
    }
    
    @Test
    void getById_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/vehicles/{id}", vehicleEntity.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.productionYear").value(vehicleEntity.getProductionYear()));
    }
    
    @Test
    void update_ShouldReturnStatus400_WhenModelDoesNotExist() throws Exception {
        CategoryDto category = CategoryDto.builder().name(categoryEntity.getName()).build();
        VehicleDto vehicle = VehicleDto.builder().id(vehicleEntity.getId()).categories(Set.of(category)).build();
        String vehicleJson = mapper.writeValueAsString(vehicle);
        String notExistingModelName = "Pickup";
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturerEntity.getName(), 
                            notExistingModelName, 
                            PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void update_ShouldReturnStatus400_WhenManufacturerDoesNotExist() throws Exception {
        CategoryDto category = CategoryDto.builder().name(categoryEntity.getName()).build();
        VehicleDto vehicle = VehicleDto.builder().id(vehicleEntity.getId()).categories(Set.of(category)).build();
        String vehicleJson = mapper.writeValueAsString(vehicle);
        String notExistingManufacturerName = "Ford";
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            notExistingManufacturerName, 
                            modelEntity.getName(), 
                            PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void update_ShouldReturnStatus400_WhenVehicleDoesNotExist() throws Exception {
        String notExistingId = "1";
        VehicleDto vehicle = VehicleDto.builder().id(notExistingId).build();
        String vehicleJson = mapper.writeValueAsString(vehicle);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturerEntity.getName(), 
                            modelEntity.getName(), 
                            PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void update_ShouldReturnStatus400_WhenMethodArgumentNotValid() throws Exception {
        VehicleDto vehicle = VehicleDto.builder().build();
        String vehicleJson = mapper.writeValueAsString(vehicle);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturerEntity.getName(), 
                            modelEntity.getName(), 
                            PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void update_ShouldReturnStatusIsOk() throws Exception {
        CategoryDto category = CategoryDto.builder().name(categoryEntity.getName()).build();
        VehicleDto vehicle = VehicleDto.builder().id(vehicleEntity.getId()).categories(Set.of(category)).build();
        String vehicleJson = mapper.writeValueAsString(vehicle);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturerEntity.getName(), 
                            modelEntity.getName(), 
                            PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleJson))
               .andExpect(status().isOk())
               .andExpect(header().string("Location", containsString("/v1/vehicles/")));
        
        Vehicle persistedVehicle = vehicleRepository.findById(vehicleEntity.getId()).orElseThrow();
        
        assertEquals(category.getName(), persistedVehicle.getCategories().iterator().next().getName());
        assertEquals(manufacturerEntity.getName(), persistedVehicle.getManufacturer().getName());
        assertEquals(modelEntity.getName(), persistedVehicle.getModel().getName());
        assertEquals(PRODUCTION_YEAR, persistedVehicle.getProductionYear());
    }
    
    @Test
    void deleteById_ShouldReturnStatus204() throws Exception {
        String notExistingId = "1";
        mockMvc.perform(delete("/v1/vehicles/{id}", notExistingId))
               .andExpect(status().is(204));
    }
    
    @Test
    void deleteById_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(delete("/v1/vehicles/{id}", vehicleEntity.getId()))
               .andExpect(status().isOk());
        
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleEntity.getId());
        
        assertTrue(vehicleOpt.isEmpty());
    }
}
