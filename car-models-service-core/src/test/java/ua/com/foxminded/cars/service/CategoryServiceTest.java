package ua.com.foxminded.cars.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ua.com.foxminded.cars.dto.CategoryDto;
import ua.com.foxminded.cars.entity.Category;
import ua.com.foxminded.cars.exception.AlreadyExistsException;
import ua.com.foxminded.cars.exception.NotFoundException;
import ua.com.foxminded.cars.mapper.CategoryMapper;
import ua.com.foxminded.cars.mapper.CategoryMapperImpl;
import ua.com.foxminded.cars.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    
    public static final String CATEGORY_NAME = "Sedan";
    
    @InjectMocks
    private CategoryService categoryService;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Spy
    private CategoryMapper categoryMapper = new CategoryMapperImpl();
    
    private Category category;
    private CategoryDto categoryDto;
    
    @BeforeEach
    void SetUp() {
        category = Category.builder().name(CATEGORY_NAME).build();
        categoryDto = CategoryDto.builder().name(CATEGORY_NAME).build();
    }
    
    @Test
    void save_ShouldSaveCategory() {
        when(categoryRepository.existsById(CATEGORY_NAME)).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        categoryService.save(categoryDto);
        
        verify(categoryRepository).existsById(CATEGORY_NAME);
        verify(categoryMapper).map(categoryDto);
        verify(categoryRepository).save(category);
        verify(categoryMapper).map(category);
    }
    
    @Test
    void save_ShouldThrow_WhenSuchCategoryAlreadyExists() {
        when(categoryRepository.existsById(categoryDto.getName())).thenReturn(true);
        
        assertThrows(AlreadyExistsException.class, () -> categoryService.save(categoryDto));
    }
    
    @Test
    void getAll_ShouldGetCategories() {
        Pageable pageable = Pageable.unpaged();
        List<Category> categories = Arrays.asList(category);
        Page<Category> categoriesPage = new PageImpl<>(categories);
        when(categoryRepository.findAll(pageable)).thenReturn(categoriesPage);
        categoryService.getAll(pageable);
        
        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).map(category);
    }
    
    @Test
    void deleteByName_ShouldDeleteCategory() {
        when(categoryRepository.findById(CATEGORY_NAME)).thenReturn(Optional.of(category));
        categoryService.deleleteByName(CATEGORY_NAME);
        
        verify(categoryRepository).findById(CATEGORY_NAME);
        verify(categoryRepository).deleteById(CATEGORY_NAME);
    }
    
    @Test
    void deleteByName_ShouldThrow_WhenNoSuchCategory() {
        when(categoryRepository.findById(CATEGORY_NAME)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> categoryService.deleleteByName(CATEGORY_NAME));
    }

    @Test
    void getByName_ShouldGetCategory() {
        when(categoryRepository.findById(CATEGORY_NAME)).thenReturn(Optional.of(category));
        categoryService.getByName(CATEGORY_NAME);
        
        verify(categoryRepository).findById(CATEGORY_NAME);
        verify(categoryMapper).map(category);
    }
}
