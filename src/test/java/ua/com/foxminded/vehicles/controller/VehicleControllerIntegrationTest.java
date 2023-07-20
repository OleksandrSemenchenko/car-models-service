package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.vehicles.controller.ExceptionHandlerController.PAGE_NUMBER_DEF;
import static ua.com.foxminded.vehicles.controller.ExceptionHandlerController.PAGE_SIZE_DEF;
import static ua.com.foxminded.vehicles.controller.VehicleController.CATEGORY_NAMES;
import static ua.com.foxminded.vehicles.controller.VehicleController.PRODUCTION_YEAR_FIELD;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
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
import ua.com.foxminded.vehicles.entitymother.CategoryMother;
import ua.com.foxminded.vehicles.entitymother.ManufacturerMother;
import ua.com.foxminded.vehicles.entitymother.ModelMother;
import ua.com.foxminded.vehicles.entitymother.VehicleMother;
import ua.com.foxminded.vehicles.repository.CategoryRepository;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;
import ua.com.foxminded.vehicles.repository.ModelRepository;
import ua.com.foxminded.vehicles.repository.VehicleRepository;

@SpringBootTest
@ActiveProfiles("test")
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
    private Model modelEntity;
    private Manufacturer manufacturerEntity;
    private Category categoryEntity;
    private CategoryDto category;
    private ObjectMapper mapper;
    
    @BeforeTransaction
    void init() {
        mapper = new ObjectMapper();

        categoryEntity = CategoryMother.complete().build();
        categoryRepository.saveAndFlush(categoryEntity);
        category = CategoryDto.builder().name(categoryEntity.getName()).build();
        
        modelEntity = ModelMother.complete().build();
        modelRepository.saveAndFlush(modelEntity);
        
        manufacturerEntity = ManufacturerMother.complete().build();
        manufacturerRepository.saveAndFlush(manufacturerEntity);

        vehicleEntity = VehicleMother.complete().build();
        vehicleRepository.saveAndFlush(vehicleEntity);
    }
    
    @Test
    void save_ShouldPersistVehicleData() throws Exception {
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             manufacturerEntity.getName(), 
                             modelEntity.getName(), 
                             String.valueOf(PRODUCTION_YEAR))
                    .param(CATEGORY_NAMES, categoryEntity.getName()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$..productionYear[1]", is(PRODUCTION_YEAR)));
    }
    
    
    @Test
    void getAll_ShouldReturnVehiclesPage() throws Exception {
        mockMvc.perform(get("/v1/vehicles/page").param("page", String.valueOf(PAGE_NUMBER_DEF))
                                                .param("size", String.valueOf(PAGE_SIZE_DEF))
                                                .param("sort", new StringBuilder().append(PRODUCTION_YEAR_FIELD)
                                                                                  .append(",")
                                                                                  .append(Sort.Direction.DESC)
                                                                                  .toString()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath(".productionYear").value(vehicleEntity.getProductionYear()));
    }
    
    @Test
    void getById_ShouldReturnVehicle() throws Exception {
        mockMvc.perform(get("/v1/vehicles/{id}", vehicleEntity.getId()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath(".productionYear").value(vehicleEntity.getProductionYear()));
    }
    
    @Test
    void update_ShouldReturnUpdatedVehicle() throws Exception {
        CategoryDto category = CategoryDto.builder().name(categoryEntity.getName()).build();
        VehicleDto vehicle = VehicleDto.builder().id(vehicleEntity.getId()).categories(Set.of(category)).build();
        String vehicleJson = mapper.writeValueAsString(vehicle);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            manufacturerEntity.getName(), 
                            modelEntity.getName(), 
                            PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleJson))
               .andExpect(status().is2xxSuccessful())
               .andDo(print())
               .andExpect(jsonPath("$['productionYear']", is(PRODUCTION_YEAR)));
        
        Vehicle persistedVehicle = vehicleRepository.findById(vehicleEntity.getId()).orElseThrow();
        
        assertEquals(category.getName(), persistedVehicle.getCategories().iterator().next().getName());
        assertEquals(manufacturerEntity.getName(), persistedVehicle.getManufacturer().getName());
        assertEquals(modelEntity.getName(), persistedVehicle.getModel().getName());
        assertEquals(PRODUCTION_YEAR, persistedVehicle.getProductionYear());
    }
    
    @Test
    void deleteById_ShouldDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/v1/vehicles/{id}", vehicleEntity.getId()))
               .andExpect(status().is2xxSuccessful());
        
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleEntity.getId());
        
        assertTrue(vehicleOpt.isEmpty());
    }
}
