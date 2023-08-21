package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.ModelNameDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ModelNameControllerIntegrationTest {
    
    public static final String MODEL_NAME = "A7";

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    private ModelNameDto modelNameDto;
    private String modelNameDtoJson;

    @BeforeEach
    void setUp() {
        modelNameDto = ModelNameDto.builder().name(MODEL_NAME).build();
    }
    
    @Test
    void save_ShouldReturnStatus201() throws Exception {
        String newModelName = "Fusion";
        modelNameDto.setName(newModelName);
        modelNameDtoJson = mapper.writeValueAsString(modelNameDto);
        
        mockMvc.perform(post("/v1/model-names").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelNameDtoJson))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/v1/model-names/" + newModelName)));
    }
    
    @Test
    void getByName_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/model-names/{name}", MODEL_NAME))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is(MODEL_NAME)));
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/model-names"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", Matchers.hasSize(1)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/model-names/{name}", MODEL_NAME))
               .andExpect(status().isNoContent());
    }
}
