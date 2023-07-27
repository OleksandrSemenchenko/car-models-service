package ua.com.foxminded.vehicles.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.entity.Manufacturer;
import ua.com.foxminded.vehicles.repository.ManufacturerRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ManufacturerControllerIntegrationTest {
    
    public static final String MANUFACTURER_NAME = "Audi";
    
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    
    @Autowired
    private MockMvc mockMvc;
    
    private Manufacturer manufacturerEntity;
    private ObjectMapper mapper;
    private String jsonManufacturer;
    private ManufacturerDto manufacturer;
    private List<ManufacturerDto> manufacturersList; 
    
    @BeforeTransaction
    void init() {
        manufacturerEntity = Manufacturer.builder().name(MANUFACTURER_NAME).build();
        manufacturerRepository.saveAndFlush(manufacturerEntity);
    }
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        mapper = new ObjectMapper();
        manufacturer = ManufacturerDto.builder().name(manufacturerEntity.getName()).build();
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
        mockMvc.perform(get("/v1/manufacturers"))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath(".name", MANUFACTURER_NAME).exists());
    }
    
    @Test
    void save_ShouldPersistManufacturerData() throws Exception {
        ManufacturerDto newManufacturer = ManufacturerDto.builder().name("Bradley").build();
        String newJsonManufacturer = mapper.writeValueAsString(newManufacturer);
        mockMvc.perform(post("/v1/manufacturers/manufacturer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(newJsonManufacturer))
               .andExpect(status().is2xxSuccessful())
               .andExpect(content().json(newJsonManufacturer));
    }
    
    @Test
    void deleteByName_ShouldDeleteManufacturer() throws Exception {
        mockMvc.perform(delete("/v1/manufacturers/{name}", manufacturerEntity.getName()))
               .andExpect(status().is2xxSuccessful());
        
        Optional<Manufacturer> manufacturerOptional = manufacturerRepository
                .findById(manufacturerEntity.getName());
        
        assertTrue(manufacturerOptional.isEmpty());
    }
}
