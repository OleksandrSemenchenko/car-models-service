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
package ua.com.foxminded.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.repository.specification.ModelSpecification;
import ua.com.foxminded.repository.specification.SearchFilter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ModelRepositoryTest {

  private final static String MODEL_NAME = "A7";
  private final static String MANUFACTURE_NAME = "Audi";
  private final static String NOT_EXISTING_MANUFACTURER_NAME = "Volvo";
  private final static Integer MODEL_YEAR = 2020;
  private final static Integer NOT_EXISTING_MODEL_YEAR = 2035;
  private final static String CATEGORY_NAME = "Sedan";
  private final static String NOT_EXISTING_CATEGORY_NAME = "Pickup";

  @Autowired private ModelRepository modelRepository;

  @Test
  void existsByYearValue_shouldReturnTrue_whenNoModelInDb() {
    boolean isModelExist = modelRepository.existsByYearValue(NOT_EXISTING_MODEL_YEAR);

    assertFalse(isModelExist);
  }

  @Test
  void existsByYearValue_shouldReturnTrue_whenModelIsInDb() {
    boolean isModelExist = modelRepository.existsByYearValue(MODEL_YEAR);

    assertTrue(isModelExist);
  }

  @Test
  void existsByCategoriesName_shouldReturnTrue_whenNoModelInDb() {
    boolean isModelExist = modelRepository.existsByCategoriesName(NOT_EXISTING_CATEGORY_NAME);

    assertFalse(isModelExist);
  }

  @Test
  void existsByCategoriesName_shouldReturnTrue_whenModelIsInDb() {
    boolean isModelExist = modelRepository.existsByCategoriesName(CATEGORY_NAME);

    assertTrue(isModelExist);
  }


  @Test
  void existsByManufacturer_Name_shouldReturnFalse_whenNoModelInDb() {
    boolean isModelExist = modelRepository.existsByManufacturerName(NOT_EXISTING_MANUFACTURER_NAME);

    assertFalse(isModelExist);
  }

  @Test
  void existsByManufacturer_Name_shouldReturnTrue_whenModelIsInDb() {
    boolean isModelExist = modelRepository.existsByManufacturerName(MANUFACTURE_NAME);

    assertTrue(isModelExist);
  }

  @Test
  void findByNameYearManufacturerName_shouldReturnEmptyOptional_whenModelIsInDb() {
    SearchFilter filter =
      SearchFilter.builder()
        .name(MODEL_NAME)
        .manufacturer(NOT_EXISTING_MANUFACTURER_NAME)
        .year(MODEL_YEAR)
        .build();
    Specification<Model> specification = ModelSpecification.getSpecification(filter);

    Optional<Model> modelOptional = modelRepository.findOne(specification);

    assertTrue(modelOptional.isEmpty());
  }

  @Test
  void findByNameYearManufacturerName_shouldReturnModel_whenModelIsInDb() {
    SearchFilter filter =
        SearchFilter.builder()
            .name(MODEL_NAME)
            .manufacturer(MANUFACTURE_NAME)
            .year(MODEL_YEAR)
            .build();
    Specification<Model> specification = ModelSpecification.getSpecification(filter);

    Model model = modelRepository.findOne(specification).get();

    assertEquals(MODEL_NAME, model.getName());
    assertEquals(MANUFACTURE_NAME, model.getManufacturer().getName());
    assertEquals(MODEL_YEAR, model.getYear().getValue());
  }
}
