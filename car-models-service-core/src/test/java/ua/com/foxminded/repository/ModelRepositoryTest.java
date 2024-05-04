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

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.repository.specification.ModelSpecification;
import ua.com.foxminded.repository.specification.SearchFilter;

@DataJpaTest
class ModelRepositoryTest {

  private final String MODEL_NAME = "A7";
  private final String MANUFACTURE_NAME = "Audi";
  private final Integer MODEL_YEAR = 2020;

  @Autowired private ModelRepository modelRepository;

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
