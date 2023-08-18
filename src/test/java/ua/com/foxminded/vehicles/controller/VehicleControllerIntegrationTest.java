package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
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

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.VehicleDto;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Transactional
class VehicleControllerIntegrationTest {
    
    public static final int PRODUCTION_YEAR = 2020;
    public static final String VEHICLE_ID = "1";
    
    @Autowired
    private MockMvc mockMvc;
    
    private VehicleDto vehicleDto;
    private ObjectMapper mapper;
    
    @BeforeEach
    void SetUp() {
        vehicleDto = VehicleDto.builder().id(VEHICLE_ID)
                                         .year(PRODUCTION_YEAR)
                                         .categories(Set.of(CATEGORY_NAME))
                                         .build();
        mapper = new ObjectMapper();
    }
    
    @Test
    void searchByManufacturerAndModelAndYear_ShouldReturnStaus400_WhenYearIsNotValid() throws Exception {
        int notValidYear = -2023;
        
        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, notValidYear))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void searchByManufacturerAndModelAndYear_ShouldReturnStausOk() throws Exception {
        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR))
               .andExpect(status().isOk());
    }
    
    @Test
    void search_ShouldReturnStatus400_WhenParametersAreNotValid() throws Exception {
        String notValidMaxYear = "-2023";
        String notValidMinYear = "-2021";
        
        mockMvc.perform(get("/v1/vehicles").param("maxYear", notValidMaxYear)
                                           .param("minYear", notValidMinYear))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void search_ShouldReturnStatus200_WhenNotExistingParameters() throws Exception {
        String notExistingModel = "Insight";
        String notExistingCategory = "Coupe";
        String notExistingManufacturer = "Peugeot";
        
        mockMvc.perform(get("/v1/vehicles").param("model", notExistingModel)
                                           .param("category", notExistingCategory)
                                           .param("manufacturer", notExistingManufacturer))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(0)));
    }
    
    @Test
    void search_ShouldReturnStatusIsOk_WhenParametersArePresent() throws Exception {
        mockMvc.perform(get("/v1/vehicles").param("model", MODEL_NAME)
                                           .param("category", CATEGORY_NAME)
                                           .param("manufacturer", MANUFACTURER_NAME)
                                           .param("maxYear", String.valueOf(PRODUCTION_YEAR))
                                           .param("minYear", String.valueOf(PRODUCTION_YEAR)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(1)))
               .andExpect(jsonPath("$.content[0].year", is(PRODUCTION_YEAR)))
               .andExpect(jsonPath("$.content[0].manufacturer", is(MANUFACTURER_NAME)))
               .andExpect(jsonPath("$.content[0].categories[0]", is(CATEGORY_NAME)));
    }
    
    @Test
    void search_ShouldReturnStatusIsOk_WhenNoParameters() throws Exception {
        int persistedVehiclesQuantity = 1;
        
        mockMvc.perform(get("/v1/vehicles"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(persistedVehiclesQuantity)));
    }
    
    @Test
    void getById_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/vehicles/{id}", VEHICLE_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(VEHICLE_ID));
    }
    
    @Test
    void save_ShouldReturnStatus404_WhenNoCategory() throws Exception {
        String notExistingCategoryName = "Pickup";
        vehicleDto.setCategories(Set.of(notExistingCategoryName));
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             MANUFACTURER_NAME, 
                             MODEL_NAME, 
                             PRODUCTION_YEAR).contentType(APPLICATION_JSON)
                                             .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void save_ShouldReturnStatus404_WhenNoModel() throws Exception {
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingModelName = "Mustang";
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             MANUFACTURER_NAME, 
                             notExistingModelName, 
                             PRODUCTION_YEAR).contentType(APPLICATION_JSON)
                                             .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void save_ShouldReturnStatus404_WhenNoManufacturer() throws Exception {
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingManufacturerName = "Volvo";
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             notExistingManufacturerName, 
                             MODEL_NAME, 
                             PRODUCTION_YEAR).contentType(APPLICATION_JSON)
                                             .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenArgumentsAreNotValid() throws Exception {
        vehicleDto.setCategories(null);
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notValidYear = "-2023";
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             MANUFACTURER_NAME, 
                             MODEL_NAME, 
                             notValidYear)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatus201() throws Exception {
        vehicleDto.setId(null);
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             MANUFACTURER_NAME, 
                             MODEL_NAME, 
                             PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/vehicles/")));
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoCategory() throws Exception {
        String notExistingCategoryName = "Coupe";
        vehicleDto.setCategories(Set.of(notExistingCategoryName));
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}/{id}", 
                            MANUFACTURER_NAME, 
                            MODEL_NAME, 
                            PRODUCTION_YEAR, 
                            VEHICLE_ID).contentType(APPLICATION_JSON)
                                       .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoModel() throws Exception {
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingModelName = "Pickup";
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{model}/{year}/{id}", 
                            MANUFACTURER_NAME, 
                            notExistingModelName, 
                            PRODUCTION_YEAR, 
                            VEHICLE_ID).contentType(APPLICATION_JSON)
                                       .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoManufacturer() throws Exception {
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        String notExistingManufacturerName = "Ford";
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}/{id}", 
                            notExistingManufacturerName, 
                            MODEL_NAME, 
                            PRODUCTION_YEAR, 
                            VEHICLE_ID).contentType(APPLICATION_JSON)
                                       .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNoVehicle() throws Exception {
        String notExistingId = "10";
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}/{id}", 
                            MANUFACTURER_NAME, 
                            MODEL_NAME, 
                            PRODUCTION_YEAR, 
                            notExistingId).contentType(APPLICATION_JSON)
                                          .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus400_WhenArgumentsAreNotValid() throws Exception {
        vehicleDto.setCategories(null);
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        int notValidYear = -2023;
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}/{id}", 
                            MANUFACTURER_NAME, 
                            MODEL_NAME, 
                            notValidYear, 
                            VEHICLE_ID).contentType(APPLICATION_JSON)
                                       .content(vehicleDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void update_ShouldReturnStatusIsOk() throws Exception {
        vehicleDto.setCategories(Set.of(CATEGORY_NAME));
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}/{id}", 
                            MANUFACTURER_NAME, 
                            MODEL_NAME, 
                            PRODUCTION_YEAR, 
                            VEHICLE_ID)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isOk());
    }
    
    @Test
    void deleteById_ShouldReturnStatus404_WhenNoVehicle() throws Exception {
        String notExistingId = "10";
        mockMvc.perform(delete("/v1/vehicles/{id}", notExistingId))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteById_ShouldReturnStatusIs204() throws Exception {
        mockMvc.perform(delete("/v1/vehicles/{id}", VEHICLE_ID))
               .andExpect(status().isNoContent());
    }
}
