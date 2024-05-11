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

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.exceptionhandler.exceptions.CategoryNotFoundException;
import ua.foxminded.cars.mapper.CategoryMapper;
import ua.foxminded.cars.repository.CategoryRepository;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.service.CategoryService;
import ua.foxminded.cars.service.dto.CategoryDto;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

  private final CategoryRepository categoryRepository;
  public final CategoryMapper categoryMapper;

  @Override
  public Set<CategoryDto> getCategories(Set<String> categoryNames) {
    List<Category> categories = categoryRepository.findAllById(categoryNames);
    verifyIfAllCategoriesPresent(categoryNames, categories);
    return categoryMapper.toDtoSet(categories);
  }

  private void verifyIfAllCategoriesPresent(
      Set<String> expectedNames, List<Category> actualCategories) {
    actualCategories.forEach(
        category -> {
          if (!expectedNames.contains(category.getName())) {
            throw new CategoryNotFoundException(category.getName());
          }
        });
  }

  @Override
  public void deleteCategory(String categoryName) {
    if (!categoryRepository.existsById(categoryName)) {
      throw new CategoryNotFoundException(categoryName);
    }
    categoryRepository.deleteById(categoryName);
  }

  @Override
  public void createCategoryIfNeeded(String categoryName) {
    if (!categoryRepository.existsById(categoryName)) {
      Category category = Category.builder().name(categoryName).build();
      categoryRepository.save(category);
    }
  }
}
