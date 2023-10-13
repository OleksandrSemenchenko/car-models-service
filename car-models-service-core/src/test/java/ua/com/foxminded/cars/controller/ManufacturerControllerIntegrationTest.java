package ua.com.foxminded.cars.controller;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.cars.dto.ManufacturerDto;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ManufacturerControllerIntegrationTest extends IntegrationTestContext {
    
    public static final String MANUFACTURER = "Audi";
    public static final String MANUFACTURER_WITHOUT_RELATIONS = "Ford";
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    private ManufacturerDto manufacturerDto;
    private String manufacturerDtoJson;
    
    @BeforeEach
    void setUp() {
        manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER).build();
    }
    
    @Test
    void getByName_ShouldReturnStatus200() throws Exception {
        manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(get("/v1/manufacturers/{name}", MANUFACTURER).with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isOk())
               .andExpect(content().json(manufacturerDtoJson));
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/manufacturers").with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].name", MANUFACTURER).exists());
    }
    
    @Test
    void save_ShouldReturnStatus201() throws Exception {
        String notExistingManufacturer = "Mercedes-Benz";
        manufacturerDto.setName(notExistingManufacturer);
        String manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(post("/v1/manufacturers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(manufacturerDtoJson)
                    .with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/v1/manufacturers/" + notExistingManufacturer)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/manufacturers/{name}", MANUFACTURER_WITHOUT_RELATIONS)
                    .with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isNoContent());
    }
}
