package ua.com.foxminded.vehicles.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.vehicles.controller.CategoryController.NAME;
import static ua.com.foxminded.vehicles.controller.ExceptionHandlerController.PAGE_NUMBER_DEF;
import static ua.com.foxminded.vehicles.controller.ExceptionHandlerController.PAGE_SIZE_DEF;
import static ua.com.foxminded.vehicles.entitymother.CategoryMother.CATEGORY_NAME;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.entitymother.CategoryMother;
import ua.com.foxminded.vehicles.model.CategoryDto;
import ua.com.foxminded.vehicles.repository.CategoryRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private CategoryDto category;
    private Category categoryEntity;
    private ObjectMapper mapper;
    
    @BeforeTransaction
    void init() {
        categoryEntity = CategoryMother.complete().build();
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
    void updateName() throws Exception {
        String newName = "Sedan";
        mockMvc.perform(put("/v1/categories/{name}", categoryEntity.getName())
                    .param(NAME, newName))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$['name']", is(newName)));
    }
    
    @Test
    void getAll_ShouldReturnModelsPage() throws Exception {
        mockMvc.perform(get("/v1/categories/page")
                    .param("page", String.valueOf(PAGE_NUMBER_DEF))
                    .param("size", String.valueOf(PAGE_SIZE_DEF))
                    .param("sort", new StringBuilder().append(CategoryController.NAME_FIELD)
                                                      .append(",")
                                                      .append(Sort.Direction.DESC).toString()))
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
