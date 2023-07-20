package ua.com.foxminded.vehicles.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.vehicles.controller.ExceptionHandlerController.PAGE_NUMBER_DEF;
import static ua.com.foxminded.vehicles.controller.ExceptionHandlerController.PAGE_SIZE_DEF;
import static ua.com.foxminded.vehicles.controller.ModelController.NAME_FIELD;
import static ua.com.foxminded.vehicles.entitymother.ModelMother.MODEL_NAME;

import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.entity.Model;
import ua.com.foxminded.vehicles.entitymother.ModelMother;
import ua.com.foxminded.vehicles.model.ModelDto;
import ua.com.foxminded.vehicles.repository.ModelRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ModelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelRepository modelRepository;
    
    private Model modelEntity;
    private ModelDto model;
    private String modelJson;
    private ObjectMapper mapper;
    
    @BeforeTransaction
    void init() {
        modelEntity = ModelMother.complete().build();
        modelRepository.saveAndFlush(modelEntity);
        
    }
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        model = ModelDto.builder().name(modelEntity.getName())
                               .vehicles(new HashSet<>()).build();
        mapper = new ObjectMapper();
        modelJson = mapper.writeValueAsString(model);
    }
    
    @Test
    void save_ShouldPersistModelData() throws Exception {
        model.setName("Fusion");
        String modelJson = mapper.writeValueAsString(model);
        mockMvc.perform(post("/v1/manufacturers/models/model").contentType(MediaType.APPLICATION_JSON)
                                                              .content(modelJson))
               .andExpect(status().is2xxSuccessful());
        
        Optional<Model> persistedModel = modelRepository.findById(model.getName());
        assertTrue(persistedModel.isPresent());
    }
    
    @Test
    void getByName_ShouldReturnModel() throws Exception {
        mockMvc.perform(get("/v1/manufacturers/models/{name}", modelEntity.getName()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(content().json(modelJson));
    }
    
    @Test
    void getAll_ShouldReturnModelsList() throws Exception {
        mockMvc.perform(get("/v1/manufacturers/models/page")
                    .param("page", String.valueOf(PAGE_NUMBER_DEF))
                    .param("size", String.valueOf(PAGE_SIZE_DEF))
                    .param("sort", new StringBuilder().append(NAME_FIELD)
                                                      .append(",")
                                                      .append(Sort.Direction.DESC).toString()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath(".name").value(MODEL_NAME));
    }
    
    @Test
    void updateName_ShouldUpdateModelName() throws Exception {
        String newName = "Q8";
        mockMvc.perform(put("/v1/manufacturers/models/{name}", modelEntity.getName())
                    .param("newName", newName))
               .andExpect(status().is2xxSuccessful());
        
        Optional<Model> updatedModelOpt = modelRepository.findById(newName);
        assertTrue(updatedModelOpt.isPresent());
    }
    
    @Test
    void deleteByName_ShouldDeleteModel() throws Exception {
        mockMvc.perform(delete("/v1/manufacturers/models/{name}", modelEntity.getName()))
               .andExpect(status().is2xxSuccessful());
        
        Optional<Model> modelEntityOpt = modelRepository.findById(modelEntity.getName());
        assertTrue(modelEntityOpt.isEmpty());
    }
}
