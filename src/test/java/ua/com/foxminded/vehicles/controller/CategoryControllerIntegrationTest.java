package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.test.context.transaction.BeforeTransaction;
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
    
    private CategoryDto category;
    private Category categoryEntity;
    private ObjectMapper mapper;
    
    @BeforeTransaction
    void init() {
        categoryEntity = Category.builder().name(CATEGORY_NAME).build();
        categoryRepository.saveAndFlush(categoryEntity);
    }
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        mapper = new ObjectMapper();
        category = CategoryDto.builder().name(CATEGORY_NAME).build();
    }
    
    @Test
    void deleteByName() throws Exception {
        mockMvc.perform(delete("/v1/categories/{name}", categoryEntity.getName()))
               .andExpect(status().is2xxSuccessful());
        
        Optional<Category> categoryOpt = categoryRepository.findById(categoryEntity.getName());
        
        assertTrue(categoryOpt.isEmpty());
    }
    
    @Test
    void getAll_ShouldReturnModelsPage() throws Exception {
        mockMvc.perform(get("/v1/categories"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.content[0].['name']", is(categoryEntity.getName())));
    }
    
    @Test
    void getByName_ShouldReturnModel() throws Exception {
        mockMvc.perform(get("/v1/categories/{name}", categoryEntity.getName()))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$['name']", is(categoryEntity.getName())));
    }
    
    @Test
    void save_ShouldPersistCategoryData() throws Exception {
        category.setName("Sedan");
        String categoryJson = mapper.writeValueAsString(category);
        
        mockMvc.perform(post("/v1/categories/category").contentType(APPLICATION_JSON)
                                                       .content(categoryJson))
               .andExpect(status().is2xxSuccessful())
               .andExpect(content().json(categoryJson));
    }
}
