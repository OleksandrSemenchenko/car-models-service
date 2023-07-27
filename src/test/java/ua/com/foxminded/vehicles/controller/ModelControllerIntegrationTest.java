package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.repository.ModelRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ModelControllerIntegrationTest {
    
    public static final String MODEL_NAME = "A7";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelRepository modelRepository;
    
    private Model model;
    private ModelDto modelDto;
    private String modelDtoJson;
    private ObjectMapper mapper;
    
    @BeforeTransaction
    void init() throws JsonProcessingException {
        model = Model.builder().name(MODEL_NAME).build();
        modelRepository.saveAndFlush(model);
        modelDto = ModelDto.builder().name(MODEL_NAME).build();
        mapper = new ObjectMapper();
        modelDtoJson = mapper.writeValueAsString(modelDto);
    }
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        modelDto = ModelDto.builder().name(model.getName())
                                  .vehicles(new HashSet<>()).build();
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
    void save_ShouldReturnStatus400_WhenModelAlreadyExists() throws Exception {
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatusIsOk() throws Exception {
        modelDto.setName("Fusion");
        String modelJson = mapper.writeValueAsString(modelDto);
        
        mockMvc.perform(post("/v1/models").contentType(MediaType.APPLICATION_JSON)
                                          .content(modelJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/models/" + modelDto.getName())));
        
        Optional<Model> persistedModel = modelRepository.findById(modelDto.getName());
        assertTrue(persistedModel.isPresent());
    }
    
    @Test
    void getByName_ShouldReturnStatus400_WhenNoModel() throws Exception {
        String notExistingModelName = "Camaro";
        mockMvc.perform(get("/v1/models/{name}", notExistingModelName))
               .andExpect(status().is(400));
    }
    
    @Test
    void getByName_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/models/{name}", model.getName()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is(model.getName())));
    }
    
    @Test
    void getAll_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/models"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", Matchers.hasSize(1)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus204_WhenNoModel() throws Exception {
        String notExistingModelName = "Enclave";
        mockMvc.perform(delete("/v1/models/{name}", notExistingModelName))
               .andExpect(status().is(204));
    }
    
    @Test
    void deleteByName_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(delete("/v1/models/{name}", model.getName()))
               .andExpect(status().isOk());
        
        Optional<Model> modelEntityOpt = modelRepository.findById(model.getName());
        assertTrue(modelEntityOpt.isEmpty());
    }
}
