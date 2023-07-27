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
    
    private CategoryDto categoryDto;
    private Category category;
    private ObjectMapper mapper;
    
    @BeforeTransaction
    void init() {
        category = Category.builder().name(CATEGORY_NAME).build();
        categoryRepository.saveAndFlush(category);
    }
    
    @BeforeEach
    void setUp() throws JsonProcessingException {
        mapper = new ObjectMapper();
        categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenMethodArgumentNotValid() throws Exception {
        category.setName("");
        String categoryJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatus400_WhenCategoryAlreadyExists() throws Exception {
        String categoryJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryJson))
               .andExpect(status().is(400));
    }
    
    @Test
    void save_ShouldReturnStatusOk() throws Exception {
        categoryDto.setName("SUV");
        String categoryJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryJson))
               .andExpect(status().is(201))
               .andExpect(header().string("Location", containsString("/v1/categories/" + 
                                                                     categoryDto.getName())));
    }
    
    @Test
    void getByName_ShouldReturnStatus400_WhenCategoryDoesNotExist() throws Exception {
        String notExistingCategoryName = "SUV";
        mockMvc.perform(get("/v1/categories/{name}", notExistingCategoryName))
               .andExpect(status().is(400));
    }
    
    @Test
    void getByName_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/categories/{name}", category.getName()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$['name']", is(category.getName())));
    }
    
    @Test
    void getAll_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/v1/categories"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content[0].['name']", is(category.getName())));
    }
    
    @Test
    void deleteByName_ShouldReturnStatus204_WhenCategoryDoesNotExist() throws Exception {
        String notExistingCategoryName = "SUV";
        mockMvc.perform(delete("/v1/categories/{name}", notExistingCategoryName))
               .andExpect(status().is(204));
    }
    
    @Test
    void deleteByName_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(delete("/v1/categories/{name}", category.getName()))
               .andExpect(status().isOk());
        
        Optional<Category> categoryOpt = categoryRepository.findById(category.getName());
        assertTrue(categoryOpt.isEmpty());
    }
}
