package ua.com.foxminded.vehicles.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.entity.Category;
import ua.com.foxminded.vehicles.mapper.CategoryMapper;
import ua.com.foxminded.vehicles.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    
    public static final String CATEGORY_NAME = "Sedan";
    
    @InjectMocks
    private CategoryService categoryService;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private CategoryMapper categoryMapper;
    
    private Category category;
    
    @BeforeEach
    void SetUp() {
        category = Category.builder().name(CATEGORY_NAME).build();
    }
    
    @Test
    void save_ShouldPerformCorrectCalls() {
        when(categoryRepository.existsById(anyString())).thenReturn(false);
        when(categoryMapper.map(isA(CategoryDto.class))).thenReturn(category);
        when(categoryRepository.save(isA(Category.class))).thenReturn(category);
        CategoryDto categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
        categoryService.save(categoryDto);
        
        verify(categoryRepository).existsById(anyString());
        verify(categoryMapper).map(isA(CategoryDto.class));
        verify(categoryRepository).save(isA(Category.class));
        verify(categoryMapper).map(isA(Category.class));
    }
    
    @Test
    void getAll_ShouldPreformCorrectCalls() {
        Pageable pageable = Pageable.unpaged();
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<Category>(categories));
        categoryService.getAll(pageable);
        
        verify(categoryRepository).findAll(isA(Pageable.class));
        verify(categoryMapper).map(isA(Category.class));
    }
    
    @Test
    void deleteByName_ShouldPerformCorrectCalls() {
        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
        categoryService.deleleteByName(CATEGORY_NAME);
        
        verify(categoryRepository).findById(anyString());
        verify(categoryRepository).deleteById(anyString());
    }

    @Test
    void getByName_ShouldPerformCorrectCalls() {
        when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
        categoryService.getByName(CATEGORY_NAME);
        
        verify(categoryRepository).findById(anyString());
        verify(categoryMapper).map(isA(Category.class));
    }
}
