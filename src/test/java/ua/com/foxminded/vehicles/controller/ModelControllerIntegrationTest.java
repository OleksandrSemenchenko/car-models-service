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

import ua.com.foxminded.vehicles.dto.ModelDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ModelControllerIntegrationTest {
    
    public static final String MODEL_NAME = "A7";

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    private ModelDto modelDto;
    private String modelDtoJson;

    @BeforeEach
    void setUp() {
        modelDto = ModelDto.builder().name(MODEL_NAME).build();
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenModelAlreadyExists() throws Exception {
        modelDtoJson = mapper.writeValueAsString(modelDto);
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelDtoJson))
               .andExpect(status().is(409));
    }
    
    @Test
    void save_ShouldReturnStatus201() throws Exception {
        String newModelName = "Fusion";
        modelDto.setName(newModelName);
        modelDtoJson = mapper.writeValueAsString(modelDto);
        
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelDtoJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/models/" + newModelName)));
    }
    
    @Test
    void getByName_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/models/{name}", MODEL_NAME))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is(MODEL_NAME)));
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        int persistedModelsQuantity = 1;
        mockMvc.perform(get("/v1/models"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", Matchers.hasSize(persistedModelsQuantity)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatusIs204() throws Exception {
        mockMvc.perform(delete("/v1/models/{name}", MODEL_NAME))
               .andExpect(status().isNoContent());
    }
}
