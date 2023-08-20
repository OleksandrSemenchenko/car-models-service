package ua.com.foxminded.vehicles.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.vehicles.controller.ModelControllerIntegrationTest.MODEL_NAME;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
    private ModelService modelService;
    
    private ModelDto modelDto;
    private String modelDtoJson;
    
    @BeforeEach
    void setUp() {
        modelDto = ModelDto.builder().name(MODEL_NAME).build();
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
        modelDto.setName(null);
        modelDtoJson = mapper.writeValueAsString(modelDto);
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelDtoJson))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenAlreadyExistsException() throws Exception {
        doThrow(AlreadyExistsException.class).when(modelService).save(modelDto);
        modelDtoJson = mapper.writeValueAsString(modelDto);
        
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelDtoJson))
               .andExpect(status().isConflict());
    }

    @Test
    void deleteByName_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(modelService).deleteByName(MODEL_NAME);
        
        mockMvc.perform(delete("/v1/models/{name}", MODEL_NAME))
               .andExpect(status().isNotFound());
    }
}
