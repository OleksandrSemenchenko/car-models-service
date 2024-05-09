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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.config.AppConfig;
import ua.com.foxminded.exceptionhandler.exceptions.ModelAlreadyExistsException;
import ua.com.foxminded.exceptionhandler.exceptions.ModelNotFoundException;
import ua.com.foxminded.mapper.ModelMapper;
import ua.com.foxminded.repository.ModelRepository;
import ua.com.foxminded.repository.entity.Category;
import ua.com.foxminded.repository.entity.Manufacturer;
import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.repository.entity.Year;
import ua.com.foxminded.repository.specification.ModelSpecification;
import ua.com.foxminded.repository.specification.SearchFilter;
import ua.com.foxminded.service.dto.ModelDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModelServiceImp implements ModelService {

  private static final String SEARCH_MODELS_CACHE = "searchModels";
  private static final String GET_MODEL_BY_ID_CACHE = "getModelById";
  private static final String GET_MODEL_CACHE = "getModel";

  private final ModelRepository modelRepository;
  private final ModelMapper modelMapper;
  private final AppConfig appConfig;
  private final ManufacturerService manufacturerService;
  private final YearService yearService;
  private final CategoryService categoryService;

  @Override
  @Transactional
  @Caching(
      evict = @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
      put = {
        @CachePut(
            value = GET_MODEL_CACHE,
            key = "{ 'getModel', #modelDto.manufacturer, #modelDto.name, #modelDto.year }"),
        @CachePut(value = GET_MODEL_BY_ID_CACHE, key = "{ 'getModelById', #modelDto.id }")
      })
  public ModelDto updateModel(ModelDto targetModelDto) {
    Model sourceModel =
        findModelBySpecification(
            targetModelDto.getManufacturer(), targetModelDto.getName(), targetModelDto.getYear());

    createCategoriesIfNeeded(targetModelDto.getCategories());
    Set<String> sourceCategories = getCategoryNames(sourceModel.getCategories());
    Set<String> targetCategories = targetModelDto.getCategories();
    removeModelFromCategories(sourceModel.getId(), sourceCategories, targetCategories);
    targetCategories.remove(sourceCategories);
    putModelToCategories(sourceModel.getId(), targetCategories);
    targetModelDto.setId(sourceModel.getId());
    return targetModelDto;
  }

  private void removeModelFromCategories(UUID modelId,
                                         Set<String> sourceCategories,
                                         Set<String> targetCategories) {
    for (String sourceCategory : sourceCategories) {
      if (!targetCategories.contains(sourceCategory)) {
        modelRepository.removeModelFromCategory(modelId, sourceCategory);
        deleteCategoryIfNeeded(sourceCategory);
      }
    }
  }

  private Set<String> getCategoryNames(Set<Category> categories) {
    return categories.stream()
      .map(Category::getName)
      .collect(Collectors.toSet());
  }

  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
        @CacheEvict(value = GET_MODEL_BY_ID_CACHE, key = "{ 'getModelById', #modelId }"),
        @CacheEvict(value = GET_MODEL_CACHE, allEntries = true)
      })
  public void deleteModelById(UUID modelId) {
    Model model = modelRepository.findById(modelId).orElseThrow(() -> new ModelNotFoundException(modelId));
    modelRepository.deleteById(modelId);
    deleteManufacturerIfNeeded(model.getManufacturer());
    deleteModelYearIfNeeded(model.getYear());
    deleteCategoriesIfNeeded(model.getCategories());
  }

  private void deleteManufacturerIfNeeded(Manufacturer manufacturer) {
    if (!modelRepository.existsByManufacturerName(manufacturer.getName())) {
      manufacturerService.deleteManufacturer(manufacturer.getName());
    }
  }

  private void deleteModelYearIfNeeded(Year year) {
    if (!modelRepository.existsByYearValue(year.getValue())) {
      yearService.deleteYear(year.getValue());
    }
  }

  private void deleteCategoriesIfNeeded(Set<Category> categories) {
    List<String> categoryNames = categories.stream().map(Category::getName).toList();

    for (String categoryName : categoryNames) {
      deleteCategoryIfNeeded(categoryName);
    }
  }

  private void deleteCategoryIfNeeded(String categoryName) {
    if (!modelRepository.existsByCategoriesName(categoryName)) {
      categoryService.deleteCategory(categoryName);
    }
  }

  @Override
  @Cacheable(value = GET_MODEL_BY_ID_CACHE, key = "{ #root.methodName, #modelId }")
  public ModelDto getModelById(UUID modelId) {
    Model model =
        modelRepository.findById(modelId).orElseThrow(() -> new ModelNotFoundException(modelId));
    return modelMapper.toDto(model);
  }

  @Override
  @Cacheable(value = GET_MODEL_CACHE, key = "{ #root.methodName,  #manufacturer, #name, #year }")
  public ModelDto getModel(String manufacturer, String name, int year) {
    Model model = findModelBySpecification(manufacturer, name, year);
    return modelMapper.toDto(model);
  }

  private Model findModelBySpecification(String manufacturer, String modelName, int modelYear) {
    Specification<Model> specification = buildSpecification(manufacturer, modelName, modelYear);
    return modelRepository
        .findOne(specification)
        .orElseThrow(() -> new ModelNotFoundException(manufacturer, modelName, modelYear));
  }

  @Override
  @Cacheable(value = SEARCH_MODELS_CACHE, key = "{ #root.methodName, #searchFilter, #pageable }")
  public Page<ModelDto> search(SearchFilter searchFilter, Pageable pageable) {
    pageable = setDefaultSortIfNeeded(pageable);
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
    return modelRepository
        .findAll(specification, pageable)
        .map(modelMapper::toDto);
  }

  private Pageable setDefaultSortIfNeeded(Pageable pageRequest) {
    if (pageRequest.getSort().isUnsorted()) {
      Sort defaulSort = Sort.by(appConfig.getModelSortDirection(), appConfig.getModelSortBy());
      return PageRequest.of(
          pageRequest.getPageNumber(),
          pageRequest.getPageSize(),
          pageRequest.getSortOr(defaulSort));
    }
    return pageRequest;
  }

  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
        @CacheEvict(value = GET_MODEL_BY_ID_CACHE, allEntries = true)
      },
      put = {
        @CachePut(
            value = GET_MODEL_CACHE,
            key = "{ 'getModel', #modelDto.manufacturer, #modelDto.name, #modelDto.year }")
      })
  public ModelDto createModel(ModelDto modelDto) {
    verifyIfModelExists(modelDto.getManufacturer(), modelDto.getName(), modelDto.getYear());
    manufacturerService.createManufacturerIfNeeded(modelDto.getManufacturer());
    yearService.createYearIfNeeded(modelDto.getYear());
    Model model = modelMapper.toEntity(modelDto);
    Model savedModel = modelRepository.save(model);
    createCategoriesIfNeeded(modelDto.getCategories());
    putModelToCategories(savedModel.getId(), modelDto.getCategories());
    modelDto.setId(savedModel.getId());
    return modelDto;
  }

  private void verifyIfModelExists(String manufacturerName, String modelName, int year) {
    Specification<Model> specification = buildSpecification(manufacturerName, modelName, year);
    modelRepository
      .findOne(specification)
      .ifPresent(
        entity -> {
          throw new ModelAlreadyExistsException(
            manufacturerName, modelName, year, entity.getId());
        });
  }

  private Specification<Model> buildSpecification(String manufacturer, String name, int year) {
    SearchFilter searchFilter =
      SearchFilter.builder().manufacturer(manufacturer).name(name).year(year).build();
    return ModelSpecification.getSpecification(searchFilter);
  }

  private void createCategoriesIfNeeded(Set<String> categoryNames) {
    for (String name : categoryNames) {
      categoryService.createCategoryIfNeeded(name);
    }
  }

  private void putModelToCategories(UUID modelId, Set<String> categoryNames) {
    for (String categoryName : categoryNames) {
      modelRepository.putModelToCategory(modelId, categoryName);
    }
  }
}
