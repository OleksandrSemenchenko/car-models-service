package ua.com.foxminded.cars.controller;

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
import static ua.com.foxminded.cars.controller.CategoryControllerIntegrationTest.CATEGORY_NAME;
import static ua.com.foxminded.cars.controller.ManufacturerControllerIntegrationTest.MANUFACTURER_NAME;
import static ua.com.foxminded.cars.controller.ModelNameControllerIntegrationTest.MODEL_NAME;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.cars.dto.ModelDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ModelControllerIntegrationTest {
    
    public static final int YEAR = 2020;
    public static final String MODEL_ID = "1";
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    private ModelDto modelDto;
    private String modelDtoJson;
    
    @BeforeEach
    void SetUp() {
        modelDto = ModelDto.builder().id(MODEL_ID)
                                     .year(YEAR)
                                     .categories(Set.of(CATEGORY_NAME))
                                     .build();
    }
    
    @Test
    void searchByManufacturerAndModelAndYear_ShouldReturnStaus200() throws Exception {
        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, YEAR))
               .andExpect(status().isOk());
    }
    
    @Test
    void search_ShouldReturnStatus200_WhenParametersArePresent() throws Exception {
        mockMvc.perform(get("/v1/models").param("model", MODEL_NAME)
                                         .param("category", CATEGORY_NAME)
                                         .param("manufacturer", MANUFACTURER_NAME)
                                         .param("maxYear", String.valueOf(YEAR))
                                         .param("minYear", String.valueOf(YEAR)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(1)))
               .andExpect(jsonPath("$.content[0].year", is(YEAR)))
               .andExpect(jsonPath("$.content[0].manufacturer", is(MANUFACTURER_NAME)))
               .andExpect(jsonPath("$.content[0].categories[0]", is(CATEGORY_NAME)));
    }
    
    @Test
    void search_ShouldReturnStatus200_WhenNoParameters() throws Exception {
        mockMvc.perform(get("/v1/models"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", hasSize(1)));
    }
    
    @Test
    void getById_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/models/{id}", MODEL_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(MODEL_ID));
    }
    
    @Test
    void save_ShouldReturnStatus201() throws Exception {
        int notExistingProductionYear = 2023;
        String vehicleDtoJson = mapper.writeValueAsString(modelDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturer}/models/{name}/{year}", 
                             MANUFACTURER_NAME, 
                             MODEL_NAME, 
                             notExistingProductionYear)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/v1/models/")));
    }
    
    @Test
    void update_ShouldReturnStatus200() throws Exception {
        modelDtoJson = mapper.writeValueAsString(modelDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{name}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(modelDtoJson))
               .andExpect(status().isOk());
    }
    
    @Test
    void deleteById_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/models/{id}", MODEL_ID))
               .andExpect(status().isNoContent());
    }
}
