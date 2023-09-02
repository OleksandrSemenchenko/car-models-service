package ua.com.foxminded.cars.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.cars.controller.ManufacturerControllerIntegrationTest.MANUFACTURER_NAME;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.cars.dto.ManufacturerDto;
import ua.com.foxminded.cars.exception.AlreadyExistsException;
import ua.com.foxminded.cars.exception.NotFoundException;
import ua.com.foxminded.cars.service.ManufacturerService;

@WebMvcTest(ManufacturerController.class)
class ManufacturerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    @MockBean
    private ManufacturerService manufacturerService;
    
    private ManufacturerDto manufacturerDto;
    private String manufacturerDtoJson;
    
    @BeforeEach
    void setUp() {
        manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
    }
    
    @Test
    @WithMockUser
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
        manufacturerDto.setName(null);
        manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(post("/v1/manufacturers").contentType(MediaType.APPLICATION_JSON)
                                                 .content(manufacturerDtoJson)
                                                 .with(csrf()))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void save_ShouldReturnStatus409_WhenAlreadyExcistsException() throws Exception {
        manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        doThrow(AlreadyExistsException.class).when(manufacturerService).save(manufacturerDto);
        
        mockMvc.perform(post("/v1/manufacturers").contentType(MediaType.APPLICATION_JSON)
                                                 .content(manufacturerDtoJson)
                                                 .with(csrf()))
               .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void deleteByName_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(manufacturerService).deleteByName(MANUFACTURER_NAME);
        
        mockMvc.perform(delete("/v1/manufacturers/{name}", MANUFACTURER_NAME).with(csrf()))
               .andExpect(status().isNotFound());
    }
}
