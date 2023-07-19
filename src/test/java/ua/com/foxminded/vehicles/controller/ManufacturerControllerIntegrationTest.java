package ua.com.foxminded.vehicles.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.vehicles.controller.DefaultController.PAGE_NUMBER_DEF;
import static ua.com.foxminded.vehicles.controller.DefaultController.PAGE_SIZE_DEF;
import static ua.com.foxminded.vehicles.controller.ManufacturerController.NAME_FIELD;
import static ua.com.foxminded.vehicles.controller.ManufacturerController.NEW_NAME;
import static ua.com.foxminded.vehicles.entitymother.ManufacturerEntityMother.MANUFACTURER_NAME;

import java.util.Arrays;
import java.util.List;
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

import ua.com.foxminded.vehicles.entity.ManufacturerEntity;
import ua.com.foxminded.vehicles.entitymother.ManufacturerEntityMother;
import ua.com.foxminded.vehicles.model.Manufacturer;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ManufacturerControllerIntegrationTest {
    
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    
    @Autowired
    private MockMvc mockMvc;
    
    private ManufacturerEntity manufacturerEntity;
    private ObjectMapper mapper;
    private String jsonManufacturer;
    private Manufacturer manufacturer;
    List<Manufacturer> manufacturersList; 
    
    @BeforeTransaction
    void init() {
        manufacturerEntity = ManufacturerEntityMother.complete().build();
        manufacturerRepository.saveAndFlush(manufacturerEntity);
    }
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        mapper = new ObjectMapper();
        manufacturer = Manufacturer.builder().name(manufacturerEntity.getName()).build();
        manufacturersList = Arrays.asList(manufacturer);
        jsonManufacturer = mapper.writeValueAsString(manufacturer);
    }
    
    @Test
    void getByName_ShouldReturnManufacturerObject() throws Exception {
        mockMvc.perform(get("/v1/manufacturers/{name}", manufacturerEntity.getName()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(content().json(jsonManufacturer));
    }
    
    @Test
    void getAll_ShouldReturnManufacturersList() throws Exception {
        mockMvc.perform(get("/v1/manufacturers/page").param("page", String.valueOf(PAGE_NUMBER_DEF))
                                                     .param("size", String.valueOf(PAGE_SIZE_DEF))
                                                     .param("sort", new StringBuilder().append(NAME_FIELD)
                                                                                       .append(",")
                                                                                       .append(Sort.Direction.DESC)
                                                                                       .toString()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath(".name", MANUFACTURER_NAME).exists());
    }
    
    @Test
    void save_ShouldPersistManufacturerData() throws Exception {
        Manufacturer newManufacturer = Manufacturer.builder().name("Bradley").build();
        String newJsonManufacturer = mapper.writeValueAsString(newManufacturer);
        mockMvc.perform(post("/v1/manufacturers/manufacturer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(newJsonManufacturer))
               .andExpect(status().is2xxSuccessful())
               .andExpect(content().json(newJsonManufacturer));
    }
    
    @Test
    void updateName_ShouldUpdateManufacturerName() throws Exception {
        String newName = "Bradley";
        mockMvc.perform(put("/v1/manufacturers/{name}", manufacturerEntity.getName())
                    .param(NEW_NAME, newName))
               .andExpect(status().is2xxSuccessful());
        
        ManufacturerEntity updatedEntity = manufacturerRepository.findById(newName).orElseThrow();
        
        assertEquals(newName, updatedEntity.getName());
    }
    
    @Test
    void deleteByName_ShouldDeleteManufacturer() throws Exception {
        mockMvc.perform(delete("/v1/manufacturers/{name}", manufacturerEntity.getName()))
               .andExpect(status().is2xxSuccessful());
        
        Optional<ManufacturerEntity> manufacturerOptional = manufacturerRepository
                .findById(manufacturerEntity.getName());
        
        assertTrue(manufacturerOptional.isEmpty());
    }
}
