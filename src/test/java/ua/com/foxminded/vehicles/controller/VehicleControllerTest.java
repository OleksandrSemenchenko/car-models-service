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
import static ua.com.foxminded.vehicles.controller.ModelControllerIntegrationTest.MODEL_NAME;
import static ua.com.foxminded.vehicles.controller.VehicleControllerIntegrationTest.PRODUCTION_YEAR;
import static ua.com.foxminded.vehicles.controller.VehicleControllerIntegrationTest.VEHICLE_ID;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.exception.AlreadyExistsException;
import ua.com.foxminded.vehicles.exception.NotFoundException;
import ua.com.foxminded.vehicles.service.VehicleService;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    @MockBean
    private VehicleService vehicleService;
    
    private VehicleDto vehicleDto;
    private String vehicleDtoJson;
    
    @BeforeEach
    void setUp() {
        vehicleDto = VehicleDto.builder().id(VEHICLE_ID)
                                         .year(PRODUCTION_YEAR)
                                         .manufacturer(MANUFACTURER_NAME)
                                         .model(MODEL_NAME)
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
        
        mockMvc.perform(get("/v1/vehicles").param("maxYear", notValidYear)
                                           .param("minYear", notValidYear)
                                           .param("year", notValidYear))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenAlreadyExistsException() throws Exception {
        doThrow(AlreadyExistsException.class).when(vehicleService).save(vehicleDto);
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(post("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR, VEHICLE_ID)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isConflict());
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenConstrainViolationException() throws Exception {
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        int notValidYear = -2023;
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, notValidYear, VEHICLE_ID)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
        vehicleDto.setCategories(null);
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR, VEHICLE_ID)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void update_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(vehicleService).update(vehicleDto);
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);
        
        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR)
                    .contentType(APPLICATION_JSON)
                    .content(vehicleDtoJson))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void update_ShouldReturnStatus400_WhenMethodArgumenNotValidException() throws Exception {
        vehicleDto.setCategories(null);
        vehicleDtoJson = mapper.writeValueAsString(vehicleDto);

        mockMvc.perform(put("/v1/manufacturers/{manufacturers}/models/{modle}/{year}", 
                            MANUFACTURER_NAME, MODEL_NAME, PRODUCTION_YEAR)
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
        doThrow(NotFoundException.class).when(vehicleService).deleteById(VEHICLE_ID);
        
        mockMvc.perform(delete("/v1/vehicles/{id}", VEHICLE_ID))
               .andExpect(status().isNotFound());
    }
}
