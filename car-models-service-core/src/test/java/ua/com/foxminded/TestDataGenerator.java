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
package ua.com.foxminded;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import ua.com.foxminded.repository.entity.Category;
import ua.com.foxminded.repository.entity.Manufacturer;
import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.repository.entity.Year;
import ua.com.foxminded.repository.specification.ModelSpecification;
import ua.com.foxminded.repository.specification.SearchFilter;
import ua.com.foxminded.service.dto.ModelDto;

public class TestDataGenerator {

  private static final UUID MODEL_ID = UUID.fromString("2bd84edb-70aa-4e74-9a41-c0e962fd36db");
  private static final int YEAR = 2021;
  private static final String MODEL_NAME = "x6";
  private static final String MANUFACTURER_NAME = "BMW";
  private static final String CATEGORY_NAME = "Pickup";

  public static Category generateCategoryEntity() {
    return Category.builder().name(CATEGORY_NAME).build();
  }

  public static Specification<Model> generateSpecification() {
    SearchFilter searchFilter =
        SearchFilter.builder().manufacturer(MANUFACTURER_NAME).name(MODEL_NAME).year(YEAR).build();
    return ModelSpecification.getSpecification(searchFilter);
  }

  public static ModelDto generateModelDtoWithId() {
    return generateModelDto(MODEL_ID);
  }

  public static ModelDto generateModelDto(UUID modelId) {
    return ModelDto.builder()
        .id(MODEL_ID)
        .year(YEAR)
        .manufacturer(MANUFACTURER_NAME)
        .name(MODEL_NAME)
        .categories(new HashSet<>(Arrays.asList(CATEGORY_NAME)))
        .build();
  }

  public static Model generateModelEntityWithId() {
    return generateModelEntity(MODEL_ID);
  }

  public static Model generateModelEntity(UUID modelId) {
    Year year = Year.builder().value(YEAR).build();
    Category category = Category.builder().name(CATEGORY_NAME).build();
    Manufacturer manufacturer = Manufacturer.builder().name(MANUFACTURER_NAME).build();
    return Model.builder()
        .id(modelId)
        .year(year)
        .manufacturer(manufacturer)
        .name(MODEL_NAME)
        .categories(new HashSet<>(Arrays.asList(category)))
        .build();
  }
}
