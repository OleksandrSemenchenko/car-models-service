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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.ModelDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ModelControllerIntegrationTest {
    
    public static final String MODEL_NAME = "A7";

    @Autowired
    private MockMvc mockMvc;

    private ModelDto modelDto;
    private ObjectMapper mapper;
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        modelDto = ModelDto.builder().name(MODEL_NAME).build();
        mapper = new ObjectMapper();
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValid() throws Exception {
        modelDto.setName("");
        String modelDtoJson = mapper.writeValueAsString(modelDto);
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenModelAlreadyExists() throws Exception {
        String modelDtoJson = mapper.writeValueAsString(modelDto);
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelDtoJson))
               .andExpect(status().is(409));
    }
    
    @Test
    void save_ShouldReturnStatusIsOk() throws Exception {
        String newModelName = "Fusion";
        modelDto.setName(newModelName);
        String modelDtoJson = mapper.writeValueAsString(modelDto);
        
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelDtoJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/models/" + newModelName)));
    }
    
    @Test
    void getByName_ShouldReturnStatus404_WhenNoModel() throws Exception {
        String notExistingModelName = "Sonata";
        mockMvc.perform(get("/v1/models/{name}", notExistingModelName))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void getByName_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/models/{name}", MODEL_NAME))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is(MODEL_NAME)));
    }
    
    @Test
    void getAll_ShouldReturnStatusIsOk() throws Exception {
        int persistedModelsQuantity = 1;
        mockMvc.perform(get("/v1/models"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", Matchers.hasSize(persistedModelsQuantity)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus404_WhenNoModel() throws Exception {
        String notExistingModelName = "Enclave";
        mockMvc.perform(delete("/v1/models/{name}", notExistingModelName))
               .andExpect(status().is(404));
    }
    
    @Test
    void deleteByName_ShouldReturnStatusIs204() throws Exception {
        mockMvc.perform(delete("/v1/models/{name}", MODEL_NAME))
               .andExpect(status().isNoContent());
    }
}
