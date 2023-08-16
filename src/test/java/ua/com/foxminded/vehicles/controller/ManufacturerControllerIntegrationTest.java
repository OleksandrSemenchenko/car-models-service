package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import ua.com.foxminded.vehicles.dto.ManufacturerDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class ManufacturerControllerIntegrationTest {
    
    public static final String MANUFACTURER_NAME = "Audi";
    
    @Autowired
    private MockMvc mockMvc;
    
    private ManufacturerDto manufacturerDto;
    private ObjectMapper mapper;
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
        mapper = new ObjectMapper();
    }
    
    @Test
    void getByName_ShouldReturnStatus404_WhenNoManufactuer() throws Exception {
        String notExistingManufacturerName = "Ford";
        mockMvc.perform(get("/v1/manufacturers/{name}", notExistingManufacturerName))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void getByName_ShouldReturnStatusIsOk() throws Exception {
        String manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(get("/v1/manufacturers/{name}", MANUFACTURER_NAME))
               .andExpect(status().isOk())
               .andExpect(content().json(manufacturerDtoJson));
    }
    
    @Test
    void getAll_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/manufacturers"))
               .andExpect(status().is(200))
               .andExpect(jsonPath("$.content[0].name", MANUFACTURER_NAME).exists());
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValid() throws Exception {
        ManufacturerDto manufacturerDto = ManufacturerDto.builder().name("").build();
        String manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(post("/v1/manufacturers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(manufacturerDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenModelAlreadyExists() throws Exception {
        String manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(post("/v1/manufacturers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(manufacturerDtoJson))
               .andExpect(status().is(409));
    }
    
    @Test
    void save_ShouldReturnStatusIsOk() throws Exception {
        String manufacturerName = "Ford";
        ManufacturerDto manufacturerDto = ManufacturerDto.builder().name(manufacturerName).build();
        String manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(post("/v1/manufacturers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(manufacturerDtoJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/manufacturers/" + manufacturerName)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus404_WhenNoManufacturer() throws Exception {
        String notExistingManufacturerName = "Ford";
        mockMvc.perform(delete("/v1/manufacturers/{name}", notExistingManufacturerName))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteByName_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/manufacturers/{name}", MANUFACTURER_NAME))
               .andExpect(status().isNoContent());
    }
}
