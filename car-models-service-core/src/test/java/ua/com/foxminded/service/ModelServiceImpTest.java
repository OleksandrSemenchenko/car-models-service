/*
 * Copyright 2023 Oleksandr Semenchenko
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
package ua.com.foxminded.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import ua.com.foxminded.TestDataGenerator;
import ua.com.foxminded.exceptionhandler.exceptions.ModelNotFoundException;
import ua.com.foxminded.mapper.CategoryMapper;
import ua.com.foxminded.mapper.ModelMapper;
import ua.com.foxminded.repository.CategoryRepository;
import ua.com.foxminded.repository.ManufacturerRepository;
import ua.com.foxminded.repository.ModelRepository;
import ua.com.foxminded.repository.entity.Category;
import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.service.dto.ModelDto;

@ExtendWith(MockitoExtension.class)
class ModelServiceImpTest {

  private static final String MODEL_MAPPER_FIELD = "modelMapper";
  private static final String CATEGORY_MAPPER_FIELD = "categoryMapper";
  private static final UUID NEW_MODEL_ID = UUID.randomUUID();
  private static final UUID MODEL_ID = UUID.randomUUID();
  private static final int YEAR = 2020;
  private static final int NEW_YEAR = 2010;
  private static final String MODEL_NAME = "A7";
  private static final String MANUFACTURER_NAME = "Audi";
  private static final String CATEGORY_NAME = "Sedan";

  @InjectMocks private ModelServiceImp modelService;

  @Mock private ModelRepository modelRepository;

  @Mock private ManufacturerRepository manufacturerRepository;

  @Mock private CategoryRepository categoryRepository;

  @BeforeEach
  void setUp() {
    ModelMapper modelMapper = Mappers.getMapper(ModelMapper.class);
    CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
    ReflectionTestUtils.setField(modelMapper, CATEGORY_MAPPER_FIELD, categoryMapper);
    ReflectionTestUtils.setField(modelService, MODEL_MAPPER_FIELD, modelMapper);
  }

  // TODO deleteModelById

  void deleteModelById_shouldDeleteModel_whenNoNeedToDeleteRelatedEntities() {
    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.empty());



  }

  @Test
  void deleteModelById_shouldThrowModelNotFoundException_whenNoModelInDb() {
    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.empty());

    assertThrows(ModelNotFoundException.class, () -> modelService.deleteModelById(MODEL_ID));
  }

  @Test
  void updateModel_shouldUpdateModel_whenCategoryIsInDb() {
    Model model = TestDataGenerator.generateModelEntityWithId();
    ModelDto modelDto = TestDataGenerator.generateModelDtoWithId();
    Category category = TestDataGenerator.generateCategoryEntity();

    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.of(model));
    when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));
    when(modelRepository.save(any(Model.class))).thenReturn(model);

    ModelDto updatedModel = modelService.updateModel(modelDto);

    verifyResults(modelDto, updatedModel);
  }

  @Test
  void updateModel_shouldUpdateModel_whenNoCategoriesInDb() {
    Model model = TestDataGenerator.generateModelEntityWithId();
    ModelDto modelDto = TestDataGenerator.generateModelDtoWithId();
    Category category = TestDataGenerator.generateCategoryEntity();

    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.of(model));
    when(categoryRepository.findById(anyString())).thenReturn(Optional.empty());
    when(categoryRepository.save(any(Category.class))).thenReturn(category);
    when(modelRepository.save(any(Model.class))).thenReturn(model);

    ModelDto updatedModel = modelService.updateModel(modelDto);

    verifyResults(modelDto, updatedModel);
  }

  private void verifyResults(ModelDto expectedModel, ModelDto actualModel) {
    String expectedCategory = expectedModel.getCategories().iterator().next();
    String actualCategory = actualModel.getCategories().iterator().next();

    assertEquals(expectedModel.getId(), actualModel.getId());
    assertEquals(expectedModel.getManufacturer(), actualModel.getManufacturer());
    assertEquals(expectedModel.getYear(), actualModel.getYear());
    assertEquals(expectedCategory, actualCategory);
  }

  @Test
  void updateModel_shouldThrowModelNotFoundException_whenNoModelInDb() {
    ModelDto modelDto = TestDataGenerator.generateModelDtoWithId();

    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
        .thenReturn(Optional.empty());
    assertThrows(ModelNotFoundException.class, () -> modelService.updateModel(modelDto));
  }

  //  @Test
  //  void getByManufacturerAndNameAndYear_ShouldThrowNotFoundException_WhenNoSuchModel() {
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.empty());
  //
  //    assertThrows(
  //        NotFoundException.class,
  //        () -> modelService.getModel(MANUFACTURER_NAME, MODEL_NAME, NEW_YEAR));
  //  }
  //
  //  @Test
  //  void getByManufacturerAndNameAndYear_ShouldReturnModel() {
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.of(model));
  //    when(modelMapper.toDto(model)).thenReturn(modelDto);
  //    modelService.getModel(MANUFACTURER_NAME, MODEL_NAME, YEAR);
  //
  //    verify(modelRepository).findOne(ArgumentMatchers.<Specification<Model>>any());
  //    verify(modelMapper).toDto(isA(Model.class));
  //  }
  //
  //  @Test
  //  void search_ShouldSeachModels() {
  //    SearchFilter seachFilter = new SearchFilter();
  //    Pageable pageable = Pageable.unpaged();
  //    Page<Model> modelsPage = new PageImpl<Model>(Arrays.asList(model));
  //    when(modelRepository.findAll(ArgumentMatchers.<Specification<Model>>any(),
  // isA(Pageable.class)))
  //        .thenReturn(modelsPage);
  //    modelService.search(seachFilter, pageable);
  //
  //    verify(modelRepository)
  //        .findAll(ArgumentMatchers.<Specification<Model>>any(), isA(Pageable.class));
  //    verify(modelMapper).toDto(model);
  //  }
  //
  //  @Test
  //  void create_Model_ShouldSaveModel() {
  //    model.setId(null);
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.empty());
  //    when(categoryRepository.findById(modelDto.getCategories().iterator().next()))
  //        .thenReturn(Optional.of(category));
  //    when(manufacturerRepository.findById(modelDto.getManufacturer()))
  //        .thenReturn(Optional.of(manufacturer));
  //    when(modelRepository.save(model)).thenReturn(model);
  //    modelService.createModel(modelDto);
  //
  //    verify(modelRepository).findOne(ArgumentMatchers.<Specification<Model>>any());
  //    verify(categoryRepository).findById(modelDto.getCategories().iterator().next());
  //    verify(modelRepository).save(model);
  //    verify(modelMapper).toDto(model);
  //  }
  //
  //  @Test
  //  void create_Model_ShouldThrowNotFoundException_WhenNoSuchModelName() {
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.empty());
  //    when(categoryRepository.findById(modelDto.getCategories().iterator().next()))
  //        .thenReturn(Optional.of(category));
  //    when(manufacturerRepository.findById(modelDto.getManufacturer()))
  //        .thenReturn(Optional.of(manufacturer));
  //
  //    assertThrows(NotFoundException.class, () -> modelService.createModel(modelDto));
  //  }
  //
  //  @Test
  //  void create_Model_ShouldThrowNotFoundException_WhenNoSuchManufacturer() {
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.empty());
  //    when(categoryRepository.findById(modelDto.getCategories().iterator().next()))
  //        .thenReturn(Optional.of(category));
  //
  // when(manufacturerRepository.findById(modelDto.getManufacturer())).thenReturn(Optional.empty());
  //
  //    assertThrows(NotFoundException.class, () -> modelService.createModel(modelDto));
  //  }
  //
  //  @Test
  //  void create_Model_ShouldThrowNotFoundException_WhenNoSuchCategory() {
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.empty());
  //    when(categoryRepository.findById(modelDto.getCategories().iterator().next()))
  //        .thenReturn(Optional.empty());
  //
  //    assertThrows(NotFoundException.class, () -> modelService.createModel(modelDto));
  //  }
  //
  //  @Test
  //  void create_Model_ShouldThrowAlreadyExistsException_WhenModelAlreadyExists() {
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.of(model));
  //
  //    assertThrows(AlreadyExistsException.class, () -> modelService.createModel(modelDto));
  //  }
  //
  //  @Test
  //  void update_ShouldThrowNotFoundException_WhenNoRequiredCategory() {
  //    String updatedCategoryName = "Pickup";
  //    modelDto.setCategories(Set.of(updatedCategoryName));
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.of(model));
  //    when(categoryRepository.findById(modelDto.getCategories().iterator().next()))
  //        .thenReturn(Optional.empty());
  //
  //    assertThrows(NotFoundException.class, () -> modelService.updateModelPartly(modelDto));
  //  }
  //
  //  @Test
  //  void update_ShouldThrowNotFoundException_WhenNoSuchModel() {
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.empty());
  //
  //    assertThrows(NotFoundException.class, () -> modelService.updateModelPartly(modelDto));
  //  }
  //
  //  @Test
  //  void update_ShouldUpdateModelPartly() {
  //    String updatedCategoryName = "Pickup";
  //    Category updatedCategory =
  //        Category.builder().name(updatedCategoryName).models(new HashSet<>()).build();
  //    modelDto.setCategories(Set.of(updatedCategoryName));
  //    when(modelRepository.findOne(ArgumentMatchers.<Specification<Model>>any()))
  //        .thenReturn(Optional.of(model));
  //    when(categoryRepository.findById(modelDto.getCategories().iterator().next()))
  //        .thenReturn(Optional.of(updatedCategory));
  //    Model updatedVehicle =
  //        Model.builder()
  //            .id(model.getId())
  //            .year(year)
  //            .manufacturer(manufacturer)
  //            .name(MODEL_NAME)
  //            .categories(new HashSet<Category>(Arrays.asList(updatedCategory)))
  //            .build();
  //    when(modelRepository.save(updatedVehicle)).thenReturn(updatedVehicle);
  //    modelService.updateModelPartly(modelDto);
  //
  //    verify(modelRepository).findOne(ArgumentMatchers.<Specification<Model>>any());
  //    verify(categoryRepository).findById(modelDto.getCategories().iterator().next());
  //    verify(modelRepository).save(updatedVehicle);
  //    verify(modelMapper).toDto(updatedVehicle);
  //  }
  //
  //  @Test
  //  void deleteModelById_ShouldThrowNotFoundException_WhenNoModel() {
  //    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.empty());
  //
  //    assertThrows(NotFoundException.class, () -> modelService.deleteModelById(MODEL_ID));
  //  }
  //
  //  @Test
  //  void deleteById_ShouldDeleteModelModel() {
  //    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.of(model));
  //    modelService.deleteModelById(MODEL_ID);
  //
  //    verify(modelRepository).findById(MODEL_ID);
  //    verify(modelRepository).deleteById(MODEL_ID);
  //  }
  //
  //  @Test
  //  void getById_ShouldThrowNotFoundException_WhenNoModelWithSuchId() {
  //    when(modelRepository.findById(NEW_MODEL_ID)).thenReturn(Optional.empty());
  //
  //    assertThrows(NotFoundException.class, () -> modelService.getModelById(NEW_MODEL_ID));
  //  }
  //
  //  @Test
  //  void getById_ShouldReturnModel() {
  //    when(modelRepository.findById(MODEL_ID)).thenReturn(Optional.of(model));
  //    when(modelMapper.toDto(model)).thenReturn(modelDto);
  //    modelService.getModelById(MODEL_ID);
  //
  //    verify(modelRepository).findById(MODEL_ID);
  //    verify(modelMapper).toDto(model);
  //  }
}
