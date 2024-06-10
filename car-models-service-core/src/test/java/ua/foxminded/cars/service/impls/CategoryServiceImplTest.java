package ua.foxminded.cars.service.impls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import ua.foxminded.cars.TestDataGenerator;
import ua.foxminded.cars.config.SortingConfig;
import ua.foxminded.cars.exceptionhandler.exceptions.CategoryAlreadyExistsException;
import ua.foxminded.cars.exceptionhandler.exceptions.CategoryNotFoundException;
import ua.foxminded.cars.mapper.CategoryMapper;
import ua.foxminded.cars.repository.CategoryRepository;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.service.dto.CategoryDto;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

  private static final String CATEGORY_NAME = "Pickup";
  private static final int PAGE_SIZE = 5;
  private static final int PAGE_NUMBER = 1;

  @InjectMocks private CategoryServiceImpl categoryService;

  @Mock private CategoryRepository categoryRepository;

  @Mock private SortingConfig sortingConfig;

  @BeforeEach
  void setUp() {
    CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
    ReflectionTestUtils.setField(categoryService, "categoryMapper", categoryMapper);
  }

  @Test
  void getAllCategories_shouldReturnSortedPage_whenRequestHasSorting() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE, Sort.Direction.DESC, "name");

    Category category = TestDataGenerator.generateCategoryEntity();
    Page<Category> categoriesPage = new PageImpl<>(List.of(category));
    CategoryDto expectedCategoryDto = TestDataGenerator.generateCategoryDto();

    when(sortingConfig.getCategorySortBy()).thenReturn("id");
    when(sortingConfig.getCategorySortDirection()).thenReturn(Sort.Direction.ASC);
    when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoriesPage);

    Page<CategoryDto> receivedCategoryDtosPage = categoryService.getAllCategories(pageable);

    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(categoryRepository).findAll(captor.capture());
    Sort sort = captor.getValue().getSort();
    assertTrue(sort.isSorted());
    assertEquals(sort.iterator().next().getDirection(), Sort.Direction.DESC);
    assertEquals(expectedCategoryDto, receivedCategoryDtosPage.getContent().get(0));
  }

  @Test
  void getAllCategories_shouldReturnSortedPage_whenRequestWithoutSorting() {
    Pageable pageable = Pageable.ofSize(5);
    Category category = TestDataGenerator.generateCategoryEntity();
    Page<Category> categoriesPage = new PageImpl<>(List.of(category));
    CategoryDto expectedCategoryDto = TestDataGenerator.generateCategoryDto();

    when(sortingConfig.getCategorySortBy()).thenReturn("name");
    when(sortingConfig.getCategorySortDirection()).thenReturn(Sort.Direction.ASC);
    when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoriesPage);

    Page<CategoryDto> receivedCategoryDtosPage = categoryService.getAllCategories(pageable);

    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(categoryRepository).findAll(captor.capture());
    Sort sort = captor.getValue().getSort();
    assertTrue(sort.isSorted());
    assertEquals(sort.iterator().next().getDirection(), Sort.Direction.ASC);
    assertEquals(expectedCategoryDto, receivedCategoryDtosPage.getContent().get(0));
  }

  @Test
  void isCategoryExist_shouldReturnFalse_whenCategoryIsInDb() {
    when(categoryRepository.existsById(CATEGORY_NAME)).thenReturn(false);

    boolean isCategoryExist = categoryService.isCategoryExist(CATEGORY_NAME);

    assertFalse(isCategoryExist);
  }

  @Test
  void isCategoryExist_shouldReturnTrue_whenCategoryIsInDb() {
    when(categoryRepository.existsById(CATEGORY_NAME)).thenReturn(true);

    boolean isCategoryExist = categoryService.isCategoryExist(CATEGORY_NAME);

    assertTrue(isCategoryExist);
  }

  @Test
  void getCategories_shouldReturnCategories_whenCategoriesAreInDb() {
    List<String> categoryNames = List.of(CATEGORY_NAME);
    Category category = TestDataGenerator.generateCategoryEntity();
    List<Category> categories = List.of(category);
    CategoryDto expectedCategoryDto = TestDataGenerator.generateCategoryDto();
    List<CategoryDto> expectedCategoryDtos = List.of(expectedCategoryDto);

    when(categoryRepository.findAllById(categoryNames)).thenReturn(categories);

    List<CategoryDto> receivedCategoryDtos = categoryService.getCategories(categoryNames);

    assertEquals(expectedCategoryDtos, receivedCategoryDtos);
  }

  @Test
  void getCategories_shouldThrowCategoryNotFoundException_whenNoCategoriesInDb() {
    List<String> categoryNames = List.of(CATEGORY_NAME);
    List<Category> categories = Collections.emptyList();

    when(categoryRepository.findAllById(categoryNames)).thenReturn(categories);

    assertThrows(
        CategoryNotFoundException.class, () -> categoryService.getCategories(categoryNames));
  }

  @Test
  void deleteCategory_shouldDeleteCategory_whenCategoryIsInDb() {
    when(categoryRepository.existsById(CATEGORY_NAME)).thenReturn(true);

    categoryService.deleteCategory(CATEGORY_NAME);

    verify(categoryRepository).deleteById(CATEGORY_NAME);
  }

  @Test
  void deleteCategory_shouldThrowCategoryNotFoundException_whenNoCategoryInDb() {
    when(categoryRepository.existsById(CATEGORY_NAME)).thenReturn(false);

    assertThrows(
        CategoryNotFoundException.class, () -> categoryService.deleteCategory(CATEGORY_NAME));
  }

  @Test
  void createCategories_shouldCreateAndReturnCategories_whenNoCategoriesInDb() {
    CategoryDto categoryDto = TestDataGenerator.generateCategoryDto();
    List<CategoryDto> categoryDtos = List.of(categoryDto);

    Category category = TestDataGenerator.generateCategoryEntity();
    List<Category> categories = List.of(category);

    when(categoryRepository.findAllById(ArgumentMatchers.<List<String>>any()))
        .thenReturn(Collections.emptyList());
    when(categoryRepository.saveAllAndFlush(ArgumentMatchers.<List<Category>>any()))
        .thenReturn(categories);

    List<CategoryDto> createdCategories = categoryService.createCategories(categoryDtos);

    assertEquals(categoryDtos, createdCategories);
  }

  @Test
  void createCategories_shouldThrowCategoryAlreadyExistException_whenCategoriesAreInDb() {
    Category category = TestDataGenerator.generateCategoryEntity();
    List<Category> categories = List.of(category);
    CategoryDto categoryDto = TestDataGenerator.generateCategoryDto();
    List<CategoryDto> categoryDtos = List.of(categoryDto);

    when(categoryRepository.findAllById(ArgumentMatchers.<List<String>>any()))
        .thenReturn(categories);

    Assertions.assertThrows(
        CategoryAlreadyExistsException.class, () -> categoryService.createCategories(categoryDtos));
  }
}
