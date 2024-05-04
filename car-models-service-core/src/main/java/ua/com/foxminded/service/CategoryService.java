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

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.exceptionhandler.exceptions.CategoryAlreadyExistsException;
import ua.com.foxminded.exceptionhandler.exceptions.CategoryNotFoundException;
import ua.com.foxminded.mapper.CategoryMapper;
import ua.com.foxminded.repository.CategoryRepository;
import ua.com.foxminded.repository.entity.Category;
import ua.com.foxminded.service.dto.CategoryDto;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private static final String CATEGORIES_CACHE = "categories";

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Transactional
  @CacheEvict(value = CATEGORIES_CACHE, key = "'getAll'")
  @CachePut(value = CATEGORIES_CACHE, key = "{ 'getByName', #categoryDto.name }")
  public CategoryDto create(CategoryDto categoryDto) {
    if (categoryRepository.existsById(categoryDto.getName())) {
      throw new CategoryAlreadyExistsException(categoryDto.getName());
    }

    Category category = categoryMapper.toDto(categoryDto);
    Category persistedCategory = categoryRepository.save(category);
    return categoryMapper.toEntity(persistedCategory);
  }

  @Cacheable(value = CATEGORIES_CACHE, key = "#root.methodName")
  public Page<CategoryDto> getAll(Pageable pageable) {
    return categoryRepository.findAll(pageable).map(categoryMapper::toEntity);
  }

  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = CATEGORIES_CACHE, key = "{ 'getByName', #name }"),
        @CacheEvict(value = CATEGORIES_CACHE, key = "'getAll'")
      })
  public void deleleteByName(String name) {
    categoryRepository.findById(name).orElseThrow(() -> new CategoryNotFoundException(name));
    categoryRepository.deleteById(name);
  }

  @Cacheable(value = CATEGORIES_CACHE, key = "{ #root.methodName, #name }")
  public CategoryDto getByName(String name) {
    return categoryRepository
        .findById(name)
        .map(categoryMapper::toEntity)
        .orElseThrow(() -> new CategoryNotFoundException(name));
  }
}
