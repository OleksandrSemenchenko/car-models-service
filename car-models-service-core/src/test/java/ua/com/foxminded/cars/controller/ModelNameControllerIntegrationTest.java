package ua.com.foxminded.cars.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.cars.dto.ModelNameDto;

@SpringBootTest
@AutoConfigureMockMvc
class ModelNameControllerIntegrationTest extends KeycloakTestContainer {
    
    public static final String MODEL_NAME = "A7";
    public static final String MODEL_NAME_WITHOUT_FOREIGN_KEY_CONSTRAINTS = "A8";

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
    @WithMockUser
    void save_ShouldReturnStatus201() throws Exception {
        String newModelName = "Fusion";
        modelNameDto.setName(newModelName);
        modelNameDtoJson = mapper.writeValueAsString(modelNameDto);
        
        mockMvc.perform(post("/v1/model-names").contentType(MediaType.APPLICATION_JSON)
                                               .content(modelNameDtoJson)
                                               .with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/v1/model-names/" + newModelName)));
    }
    
    @Test
    void getByName_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/model-names/{name}", MODEL_NAME).with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name", is(MODEL_NAME)));
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/model-names").with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content", Matchers.hasSize(2)));
    }
    
    @Test
    @WithMockUser
    void deleteByName_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/model-names/{name}", MODEL_NAME_WITHOUT_FOREIGN_KEY_CONSTRAINTS)
                    .with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isNoContent());
    }
}
