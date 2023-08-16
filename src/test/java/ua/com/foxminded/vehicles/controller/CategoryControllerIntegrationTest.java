package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.repository.CategoryRepository;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest {
    
    public static final String CATEGORY_NAME = "Sedan";
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private CategoryDto categoryDto;
    private ObjectMapper mapper;
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        mapper = new ObjectMapper();
        categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValid() throws Exception {
        categoryDto.setName("");
        String categoryDtoJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryDtoJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatus409_WhenCategoryAlreadyExists() throws Exception {
        String categoryJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryJson))
               .andExpect(status().is(409));
    }
    
    @Test
    void save_ShouldReturnStatusIsOk() throws Exception {
        String newCategoryName = "SUV";
        categoryDto.setName(newCategoryName);
        String categoryDtoJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryDtoJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/categories/" + newCategoryName)));
    }
    
    @Test
    void getByName_ShouldReturnStatus404_WhenNoCategory() throws Exception {
        String notExistingCategoryName = "SUV";
        mockMvc.perform(get("/v1/categories/{name}", notExistingCategoryName))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void getByName_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/categories/{name}", CATEGORY_NAME))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$['name']", is(CATEGORY_NAME)));
    }
    
    @Test
    void getAll_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/categories"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].['name']", is(CATEGORY_NAME)));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus404_WhenNoCategory() throws Exception {
        String notExistingCategoryName = "SUV";
        mockMvc.perform(delete("/v1/categories/{name}", notExistingCategoryName))
               .andExpect(status().isNotFound());
    }
    
    @Test
    void deleteByName_ShouldReturnStatusIs204() throws Exception {
        mockMvc.perform(delete("/v1/categories/{name}", CATEGORY_NAME))
               .andExpect(status().isNoContent());
        
        Optional<Category> categoryOptional = categoryRepository.findById(CATEGORY_NAME);
        assertTrue(categoryOptional.isEmpty());
    }
}
