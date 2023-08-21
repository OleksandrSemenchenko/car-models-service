package ua.com.foxminded.vehicles.controller;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.vehicles.controller.CategoryControllerIntegrationTest.CATEGORY_NAME;
import static ua.com.foxminded.vehicles.controller.ManufacturerControllerIntegrationTest.MANUFACTURER_NAME;
import static ua.com.foxminded.vehicles.controller.ModelNameControllerIntegrationTest.MODEL_NAME;
import static ua.com.foxminded.vehicles.controller.ModelControllerIntegrationTest.YEAR;
import static ua.com.foxminded.vehicles.controller.ModelControllerIntegrationTest.MODEL_ID;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.exception.AlreadyExistsException;
import ua.com.foxminded.vehicles.exception.NotFoundException;
import ua.com.foxminded.vehicles.service.ModelService;

@WebMvcTest(ModelController.class)
class ModelControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    @MockBean
    private ModelService vehicleService;
    
    private ModelDto vehicleDto;
    private String vehicleDtoJson;
    
    @BeforeEach
    void setUp() {
        vehicleDto = ModelDto.builder().id(MODEL_ID)
                                       .year(YEAR)
                                       .manufacturer(MANUFACTURER_NAME)
                                       .name(MODEL_NAME)
                                       .categories(Set.of(CATEGORY_NAME))
                                       .build();
    }
    
    @Test
    void getByManufacturerAndModelAndYear_ShouldReturnStatus400_WhenConstraintViolationException() throws Exception {
        String notValidYear = "-2023";
        mockMvc.perform(get("/v1/manufacturers/{manufacturer}/models/{model}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, notValidYear))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void search_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
        String notValidYear = "-2023";
        
        mockMvc.perform(get("/v1/models").param("maxYear", notValidYear)
                                         .param("minYear", notValidYear)
                                         .param("year", notValidYear))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenAlreadyExistsException() throws Exception {
        doThrow(AlreadyExistsException.class).when(vehicleService).save(vehicleDto);
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, YEAR, MODEL_ID)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isConflict());
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenConstrainViolationException() throws Exception {
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        int notValidYear = -2023;
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, notValidYear, MODEL_ID)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
        vehicleDto.setCategories(null);
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, YEAR, MODEL_ID)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(vehicleService).update(vehicleDto);
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus400_WhenMethodArgumenNotValidException() throws Exception {
        vehicleDto.setCategories(null);
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);

        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isBadRequest());
    }
    
    
    @Test
    void update_ShouldReturnStatus400_WhenConstraintViolationException() throws Exception {
        int notValidYear = -2023;
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);

        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, notValidYear)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(vehicleService).deleteById(MODEL_ID);
        
        mockMvc.perform(delete("/v1/vehicles/{id}", MODEL_ID))
               .andExpect(status().isNotFound());
    }
}
