package ua.com.foxminded.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.dto.ManufacturerDto;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class ManufacturerControllerIntegrationTest {
    
    public static final String NEW_MANUFACTURER_NAME = "Mercedes-Benz";
    public static final String MANUFACTURER_NAME = "Audi";
    public static final String MANUFACTURER_NAME_WITHOUT_RELATIONS = "Ford";
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    private ManufacturerDto manufacturerDto;
    private String manufacturerDtoJson;
    
    @BeforeEach
    void setUp() {
        manufacturerDto = ManufacturerDto.builder().name(MANUFACTURER_NAME).build();
    }
    
    @Test
    void getByName_ShouldReturnStatus200() throws Exception {
        manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(get("/v1/manufacturers/{name}", MANUFACTURER_NAME))
               .andExpect(status().isOk())
               .andExpect(content().json(manufacturerDtoJson));
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/manufacturers"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].name", MANUFACTURER_NAME).exists());
    }
    
    @Test
    void create_ShouldReturnStatus201() throws Exception {
        manufacturerDto.setName(NEW_MANUFACTURER_NAME);
        manufacturerDtoJson = mapper.writeValueAsString(manufacturerDto);
        
        mockMvc.perform(post("/v1/manufacturers")
                    .contentType(APPLICATION_JSON)
                    .content(manufacturerDtoJson))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/v1/manufacturers/" + NEW_MANUFACTURER_NAME)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/manufacturers/{name}", MANUFACTURER_NAME_WITHOUT_RELATIONS))
               .andExpect(status().isNoContent());
    }
}
