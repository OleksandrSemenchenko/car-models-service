/*
 * Copyright 2024 Oleksandr Semenchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.foxminded.cars.service.imp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import ua.foxminded.cars.TestDataGenerator;
import ua.foxminded.cars.config.AppConfig;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelAlreadyExistsException;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelNotFoundException;
import ua.foxminded.cars.mapper.CategoryMapper;
import ua.foxminded.cars.mapper.ModelMapper;
import ua.foxminded.cars.repository.ModelRepository;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.repository.entity.Model;
import ua.foxminded.cars.repository.specification.SearchFilter;
import ua.foxminded.cars.service.CategoryService;
import ua.foxminded.cars.service.ManufacturerService;
import ua.foxminded.cars.service.YearService;
import ua.foxminded.cars.service.dto.ModelDto;

@ExtendWith(MockitoExtension.class)
class ModelServiceImpTest {

  private static final String MODEL_MAPPER_FIELD = "modelMapper";
  private static final String CATEGORY_MAPPER_FIELD = "categoryMapper";
  private static final UUID MODEL_ID = UUID.fromString("2bd84edb-70aa-4e74-9a41-c0e962fd36db");
  private static final int YEAR = 2021;
  private static final String MODEL_NAME = "x6";
  private static final String MANUFACTURER_NAME = "BMW";
  private static final String CATEGORY_NAME = "Pickup";
  private static final String NOT_NEEDED_CATEGORY = "Coupe";
  private static final String SORT_BY_NAME = "name";
  private static final int FIVE_ELEMENTS = 5;
  private static final int FIRST_PAGE = 1;

  @InjectMocks private ModelServiceImp modelService;

  @Mock private ModelRepository modelRepository;

  @Mock private ManufacturerService manufacturerService;

  @Mock private YearService yearService;

  @Mock private CategoryService categoryService;

  @Mock private AppConfig appConfig;

  @BeforeEach
  void setUp() {
    ModelMapper modelMapper = Mappers.getMapper(ModelMapper.class);
    CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
    ReflectionTestUtils.setField(modelMapper, CATEGORY_MAPPER_FIELD, categoryMapper);
    ReflectionTestUtils.setField(modelService, MODEL_MAPPER_FIELD, modelMapper);
  }

  @Test
  void updateModel_shouldDeleteCategory_whenCategoryHasNoRelatedModel() {
    ModelDto modelDto = TestDataGenerator.generateModelDtoWithId();
    Model model = TestDataGenerator.generateModelEntityWithId();
    model.setCategories(Set.of(Category.builder().name(NOT_NEEDED_CATEGORY).build()));

    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.of(model));

    ModelDto actualModelDto = modelService.updateModel(modelDto);

    verify(modelRepository).putModelToCategory(MODEL_ID, CATEGORY_NAME);
    verifyModelDto(actualModelDto);
  }

  @Test
  void updateModel_shouldCreateRelatedCategory_whenNoCategoryInDb() {
    ModelDto modelDto = TestDataGenerator.generateModelDtoWithId();
    Model model = TestDataGenerator.generateModelEntityWithId();
    model.setCategories(Set.of(Category.builder().name(NOT_NEEDED_CATEGORY).build()));

    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.of(model));

    ModelDto actualModelDto = modelService.updateModel(modelDto);

    verify(modelRepository).removeModelFromCategory(MODEL_ID, NOT_NEEDED_CATEGORY);
    verifyModelDto(actualModelDto);
  }

  @Test
  void updateModel_shouldThrowModelNotFoundException_whenNoModelInDb() {
    ModelDto modelDto = TestDataGenerator.generateModelDtoWithId();

    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.empty());

    assertThrows(ModelNotFoundException.class, () -> modelService.updateModel(modelDto));
  }

  @Test
  void deleteModelById_shouldNotDeleteRelatedEntities_whenDeletingModel() {
    Model model = TestDataGenerator.generateModelEntityWithId();

    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.of(model));
    when(modelRepository.existsByManufacturerName(MANUFACTURER_NAME)).thenReturn(true);
    when(modelRepository.existsByYearValue(YEAR)).thenReturn(true);
    when(modelRepository.existsByCategoriesName(CATEGORY_NAME)).thenReturn(true);

    modelService.deleteModelById(MODEL_ID);

    verify(modelRepository).deleteById(MODEL_ID);
  }

  @Test
  void deleteModelById_shouldDeleteRelatedEntities_whenDeletingModel() {
    Model model = TestDataGenerator.generateModelEntityWithId();

    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.of(model));
    when(modelRepository.existsByManufacturerName(MANUFACTURER_NAME)).thenReturn(false);
    when(modelRepository.existsByYearValue(YEAR)).thenReturn(false);
    when(modelRepository.existsByCategoriesName(CATEGORY_NAME)).thenReturn(false);

    modelService.deleteModelById(MODEL_ID);

    verify(modelRepository).deleteById(MODEL_ID);
    verify(manufacturerService).deleteManufacturer(MANUFACTURER_NAME);
    verify(yearService).deleteYear(YEAR);
    verify(categoryService).deleteCategory(CATEGORY_NAME);
  }

  @Test
  void deleteModelById_shouldThrowModelNotFoundException_whenNoModelInDb() {
    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.empty());

    assertThrows(ModelNotFoundException.class, () -> modelService.deleteModelById(MODEL_ID));
  }

  @Test
  void getModelById_shouldReturnModel_whenModelIsInDb() {
    Model model = TestDataGenerator.generateModelEntityWithId();

    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.of(model));

    ModelDto actualModelDto = modelService.getModelById(MODEL_ID);

    verifyModelDto(actualModelDto);
  }

  @Test
  void getModelById_shouldThrowModelNotFoundException_whenNoModelInDb() {
    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.empty());

    assertThrows(ModelNotFoundException.class, () -> modelService.getModelById(MODEL_ID));
  }

  @Test
  void getModel_shouldReturnModel_whenModelIsInDb() {
    Model model = TestDataGenerator.generateModelEntityWithId();
    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.of(model));

    ModelDto actualModel = modelService.getModel(MANUFACTURER_NAME, MODEL_NAME, YEAR);

    verifyModelDto(actualModel);
  }

  @Test
  void getModel_shouldThrowModelNotFoundException_whenNoModelInDb() {
    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.empty());

    assertThrows(
        ModelNotFoundException.class,
        () -> modelService.getModel(MANUFACTURER_NAME, MODEL_NAME, YEAR));
  }

  @Test
  void searchModel_shouldReturnPage_whenRequestHasSorting() {
    SearchFilter filter = SearchFilter.builder().manufacturer(MANUFACTURER_NAME).build();
    Sort sortByName = Sort.by(SORT_BY_NAME);
    Pageable pageable = PageRequest.of(FIRST_PAGE, FIVE_ELEMENTS, sortByName);
    Model model = TestDataGenerator.generateModelEntityWithId();
    Page<Model> modelsPage = new PageImpl<>(List.of(model));

    when(modelRepository.findAll(ArgumentMatchers.<Specification<Model>>any(), any(Pageable.class)))
        .thenReturn(modelsPage);

    Page<ModelDto> actualPage = modelService.searchModel(filter, pageable);

    ModelDto actualModelDto = actualPage.getContent().get(0);
    verifyModelDto(actualModelDto);
  }

  @Test
  void searchModel_shouldSortPage_whenRequestHasNoSorting() {
    SearchFilter filter = SearchFilter.builder().manufacturer(MANUFACTURER_NAME).build();
    Pageable pageable = Pageable.ofSize(FIVE_ELEMENTS);
    Model model = TestDataGenerator.generateModelEntityWithId();
    Page<Model> modelsPage = new PageImpl<>(List.of(model));

    when(appConfig.getModelSortDirection()).thenReturn(Sort.Direction.DESC);
    when(appConfig.getModelSortBy()).thenReturn(SORT_BY_NAME);
    when(modelRepository.findAll(ArgumentMatchers.<Specification<Model>>any(), any(Pageable.class)))
        .thenReturn(modelsPage);

    Page<ModelDto> actualPage = modelService.searchModel(filter, pageable);

    ModelDto actualModelDto = actualPage.getContent().get(0);
    verifyModelDto(actualModelDto);
  }

  @Test
  void createModel_shouldCreateModel_whenNoModelInDb() {
    ModelDto modelDto = TestDataGenerator.generateModelDtoWithId();
    Model model = TestDataGenerator.generateModelEntityWithId();

    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.empty());
    when(modelRepository.save(any(Model.class))).thenReturn(model);

    ModelDto createdModel = modelService.createModel(modelDto);

    verify(manufacturerService).createManufacturerIfNeeded(anyString());
    verify(yearService).createYearIfNeeded(anyInt());
    verify(categoryService).createCategoryIfNeeded(anyString());
    verify(modelRepository).putModelToCategory(any(UUID.class), anyString());
    verifyModelDto(createdModel);
  }

  private void verifyModelDto(ModelDto actualModelDto) {
    String actualCategory = actualModelDto.getCategories().iterator().next();
    assertEquals(MODEL_ID, actualModelDto.getId());
    assertEquals(MODEL_NAME, actualModelDto.getName());
    assertEquals(YEAR, actualModelDto.getYear());
    assertEquals(MANUFACTURER_NAME, actualModelDto.getManufacturer());
    assertEquals(CATEGORY_NAME, actualCategory);
  }

  @Test
  void createModel_shouldThrowModelAlreadyExistsException_whenModelIsInDb() {
    ModelDto modelDto = TestDataGenerator.generateModelDtoWithId();

    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.of(new Model()));

    assertThrows(ModelAlreadyExistsException.class, () -> modelService.createModel(modelDto));
  }
}
