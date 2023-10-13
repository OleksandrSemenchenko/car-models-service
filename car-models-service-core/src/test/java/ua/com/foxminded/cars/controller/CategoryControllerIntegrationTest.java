package ua.com.foxminded.cars.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import ua.com.foxminded.cars.dto.CategoryDto;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerIntegrationTest extends IntegrationTestContext {
    
    public static final String CATEGORY_NAME = "Sedan";
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;
    
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
    }
    
    @Test
    void save_ShouldReturnStatus201() throws Exception {
        String newCategoryName = "SUV";
        categoryDto.setName(newCategoryName);
        String categoryDtoJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryDtoJson)
                                              .with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isCreated())
               .andExpect(header().string("Location", containsString("/v1/categories/" + newCategoryName)));
    }
    
    @Test
    void getByName_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/categories/{name}", CATEGORY_NAME).with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$['name']", is(CATEGORY_NAME)));
    }
    
    @Test
    void getAll_ShouldReturnStatus200() throws Exception {
        mockMvc.perform(get("/v1/categories").with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].['name']", is(CATEGORY_NAME)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus204() throws Exception {
        mockMvc.perform(delete("/v1/categories/{name}", CATEGORY_NAME).with(bearerTokenFor(USER_NAME_ADMIN)))
               .andExpect(status().isNoContent());
    }
}
