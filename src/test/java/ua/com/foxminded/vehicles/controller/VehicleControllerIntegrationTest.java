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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.VehicleDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class VehicleControllerIntegrationTest {
    
    public static final int PRODUCTION_YEAR = 2020;
    public static final String VEHICLE_ID = "1";
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    private VehicleDto vehicleDto;
    private String vehicleDtoJson;
    
    @BeforeEach
    void SetUp() {
        vehicleDto = VehicleDto.builder().id(VEHICLE_ID)
                                         .year(PRODUCTION_YEAR)
                                         .categories(Set.of(CATEGORY_NAME))
                                         .build();
    }
    
    @Test
    void searchByManufacturerAndModelAndYear_ShouldReturnStaus200() throws Exception {
        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR))
               .andExpect(status().isOk());
    }
    
    @Test
    void search_ShouldReturnStatus200_WhenParametersArePresent() throws Exception {
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
    void search_ShouldReturnStatus200_WhenNoParameters() throws Exception {
        mockMvc.perform(get("/v1/vehicles"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(1)));
    }
    
    @Test
    void getById_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/vehicles/{id}", VEHICLE_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(VEHICLE_ID));
    }
    
    @Test
    void save_ShouldReturnStatus201() throws Exception {
        int notExistingProductionYear = 2023;
        String vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                             MANUFACTURER_NAME, 
                             MODEL_NAME, 
                             notExistingProductionYear)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/v1/vehicles/")));
    }
    
    @Test
    void update_ShouldReturnStatus200() throws Exception {
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isOk());
    }
    
    @Test
    void deleteById_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/vehicles/{id}", VEHICLE_ID))
               .andExpect(status().isNoContent());
    }
}
