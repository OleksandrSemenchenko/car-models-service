package ua.com.foxminded.cars.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.cars.controller.ModelNameControllerIntegrationTest.MODEL_NAME;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.cars.dto.ModelNameDto;
import ua.com.foxminded.cars.exception.AlreadyExistsException;
import ua.com.foxminded.cars.exception.NotFoundException;
import ua.com.foxminded.cars.service.ModelNameService;


@WebMvcTest(ModelNameController.class)
class ModelNameControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    @MockBean
    private ModelNameService modelNameService;
    
    private ModelNameDto modelNameDto;
    private String modelNameDtoJson;
    
    @BeforeEach
    void setUp() {
        modelNameDto = ModelNameDto.builder().name(MODEL_NAME).build();
    }
    
    @Test
    @WithMockUser
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
        modelNameDto.setName(null);
        modelNameDtoJson = mapper.writeValueAsString(modelNameDto);
        mockMvc.perform(post("/v1/model-names").contentType(APPLICATION_JSON)
                                               .content(modelNameDtoJson)
                                               .with(csrf()))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void save_ShouldReturnStatus409_WhenAlreadyExistsException() throws Exception {
        doThrow(AlreadyExistsException.class).when(modelNameService).save(modelNameDto);
        modelNameDtoJson = mapper.writeValueAsString(modelNameDto);
        
        mockMvc.perform(post("/v1/model-names").contentType(APPLICATION_JSON)
                                               .content(modelNameDtoJson)
                                               .with(csrf()))
               .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void deleteByName_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(modelNameService).deleteByName(MODEL_NAME);
        
        mockMvc.perform(delete("/v1/model-names/{name}", MODEL_NAME).with(csrf()))
               .andExpect(status().isNotFound());
    }
}
