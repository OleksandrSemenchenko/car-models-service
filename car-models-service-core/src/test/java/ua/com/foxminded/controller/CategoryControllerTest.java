package ua.com.foxminded.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.controller.CategoryControllerIntegrationTest.CATEGORY_NAME;
import static ua.com.foxminded.controller.CategoryControllerIntegrationTest.NEW_CATEGORY_NAME;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.foxminded.dto.CategoryDto;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exception.NotFoundException;
import ua.com.foxminded.service.CategoryService;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {
    
    @MockBean
    private CategoryService categoryService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;

    private CategoryDto categoryDto;
    private String categoryDtoJson;
    
    @BeforeEach
    void setUp() {
        categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
    }
    
    @Test
    void create_ShouldReturnStatus400_WhenMethodArgumentNotValidException() throws Exception {
        categoryDto.setName(null);
        categoryDtoJson = mapper.writeValueAsString(categoryDto);
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryDtoJson))
               .andExpect(status().isBadRequest());
    }
    
    @Test
    void create_ShouldReturnStatus409_WhenAlreadyExistsException() throws Exception {
        when(categoryService.create(categoryDto)).thenThrow(AlreadyExistsException.class);
        categoryDtoJson = mapper.writeValueAsString(categoryDto);
        
        mockMvc.perform(post("/v1/categories").contentType(APPLICATION_JSON)
                                              .content(categoryDtoJson))
               .andExpect(status().isConflict());
    }
    
    @Test
    void deleteByName_ShouldReturnStatus405_WhenDataIntegrityViolationException() throws Exception {
        doThrow(DataIntegrityViolationException.class).when(categoryService).deleleteByName(CATEGORY_NAME);
        
        mockMvc.perform(delete("/v1/categories/{name}", CATEGORY_NAME))
               .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void deleteByName_ShouldReturnStatus404_WhenNotFoundException() throws Exception {
        doThrow(NotFoundException.class).when(categoryService).deleleteByName(NEW_CATEGORY_NAME);
        
        mockMvc.perform(delete("/v1/categorires/{name}", NEW_CATEGORY_NAME))
               .andExpect(status().isNotFound());
    }
}
