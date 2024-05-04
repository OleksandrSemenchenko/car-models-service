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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import ua.com.foxminded.repository.CategoryRepository;
import ua.com.foxminded.repository.ManufacturerRepository;
import ua.com.foxminded.repository.ModelRepository;
import ua.com.foxminded.repository.ModelYearRepository;
import ua.com.foxminded.repository.entity.Category;
import ua.com.foxminded.repository.entity.Manufacturer;
import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.repository.entity.ModelYear;
import ua.com.foxminded.repository.specification.ModelSpecification;
import ua.com.foxminded.repository.specification.SearchFilter;
import ua.com.foxminded.service.dto.ModelDto;

@Service
@RequiredArgsConstructor
public class ModelServiceImp implements ModelService {

  private static final String SEARCH_MODELS_CACHE = "searchModels";
  private static final String GET_MODEL_BY_ID_CACHE = "getModelById";
  private static final String GET_MODEL_CACHE = "getModel";

  private final ModelRepository modelRepository;
  private final CategoryRepository categoryRepository;
  private final ManufacturerRepository manufacturerRepository;
  private final ModelYearRepository modelYearRepository;
  private final ModelMapper modelMapper;
  private final AppConfig appConfig;

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
        key = "{ 'getModel', #sourceModelDto.manufacturer, #sourceModelDto.name, #sourceModelDto.year }")
    })
  public ModelDto updateModel(ModelDto sourceModelDto, ModelDto targetModelDto) {
    Specification<Model> specification = buildSpecification(
      targetModelDto.getManufacturer(),
      targetModelDto.getName(),
      targetModelDto.getYear());
    Model model = modelRepository.findOne(specification)
            .orElseThrow(() -> new ModelNotFoundException(
              targetModelDto.getManufacturer(),
              targetModelDto.getName(),
              targetModelDto.getYear()));
    Model updatedModel = defineRelations(sourceModelDto, model);
    Model savedModel = modelRepository.save(updatedModel);
    return modelMapper.toDto(savedModel);
  }

  @Override
  @Transactional

  @Caching(
    evict = {
      @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
      @CacheEvict(value = GET_MODEL_BY_ID_CACHE, key = "{ 'getModelById', #modelId }"),
      @CacheEvict(value = GET_MODEL_CACHE, allEntries = true)
    })
  public void deleteModelById(String modelId) {
    Model model = modelRepository.findById(modelId).orElseThrow(() -> new ModelNotFoundException(modelId));
    modelRepository.deleteById(modelId);
    deleteManufacturerIfNeeded(model.getManufacturer());
    deleteCategoriesIfNeeded(model.getCategories());
    deleteModelYearIfNeeded(model.getYear());
  }

  private void deleteManufacturerIfNeeded(Manufacturer manufacturer) {
    if (!modelRepository.existsByManufacturerName(manufacturer.getName())) {
      manufacturerRepository.deleteById(manufacturer.getName());
    }
  }

  private void deleteCategoriesIfNeeded(Set<Category> categories) {
    List<String> categoryNames = categories.stream()
      .map(Category::getName).toList();

    for (String categoryName : categoryNames) {
      deleteCategoryIfNeeded(categoryName);
    }
  }

  private void deleteCategoryIfNeeded(String categoryName) {
    if (!modelRepository.existsByCategoriesName(categoryName)) {
      categoryRepository.deleteById(categoryName);
    }
  }

  private void deleteModelYearIfNeeded(ModelYear modelYear) {
    if (!modelRepository.existsByYearValue(modelYear.getValue())) {
      modelYearRepository.deleteById(modelYear.getValue());
    }
  }

  @Override
  @Cacheable(value = GET_MODEL_BY_ID_CACHE, key = "{ #root.methodName, #modelId }")
  public ModelDto getModelById(String modelId) {
    Model model =
        modelRepository.findById(modelId).orElseThrow(() -> new ModelNotFoundException(modelId));
    return modelMapper.toDto(model);
  }

  @Override
  @Cacheable(value = GET_MODEL_CACHE, key = "{ #manufacturer, #name, #year }")
  public ModelDto getModel(String manufacturer, String name, int year) {
    Specification<Model> specification = buildSpecification(manufacturer, name, year);

    return modelRepository
        .findOne(specification)
        .map(modelMapper::toDto)
        .orElseThrow(() -> new ModelNotFoundException(manufacturer, name, year));
  }

  private Specification<Model> buildSpecification(String manufacturer, String name, int year) {
    SearchFilter searchFilter =
        SearchFilter.builder().manufacturer(manufacturer).name(name).year(year).build();
    return ModelSpecification.getSpecification(searchFilter);
  }

  @Override
  @Cacheable(value = SEARCH_MODELS_CACHE, key = "{ #root.methodName, #searchFilter, #pageable }")
  public Page<ModelDto> search(SearchFilter searchFilter, Pageable pageable) {
    pageable = setDefaultSortIfNeeded(pageable);
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
    return modelRepository.findAll(specification, pageable).map(modelMapper::toDto);
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
    Model model = modelMapper.toEntity(modelDto);
    Model modelWithRelations = defineRelations(modelDto, model);
    Model savedModel = modelRepository.save(modelWithRelations);
    return modelMapper.toDto(savedModel);
  }

  private Model defineRelations(ModelDto modelDto, Model model) {
    Manufacturer manufacturer = findManufacturerByName(modelDto.getManufacturer());
    ModelYear year = findModelYearByValue(modelDto.getYear());
    Set<Category> categories = findCategories(modelDto.getCategories());
    model = modelMapper.mergeWithDto(modelDto, model);
    model.setManufacturer(manufacturer);
    model.setYear(year);
    model.addCategories(categories);
    return model;
  }

  private void verifyIfModelExists(String manufacturer, String model, int year) {
    Specification<Model> specification = buildSpecification(manufacturer, model, year);
    modelRepository
        .findOne(specification)
        .ifPresent(
            entity -> {
              throw new ModelAlreadyExistsException(entity.getId());
            });
  }

  private Manufacturer findManufacturerByName(String manufacturerName) {
    return manufacturerRepository
        .findById(manufacturerName)
        .orElseGet(
            () -> {
              Manufacturer manufacturer = Manufacturer.builder().name(manufacturerName).build();
              return manufacturerRepository.save(manufacturer);
            });
  }

  private ModelYear findModelYearByValue(int year) {
    return modelYearRepository
        .findById(year)
        .orElseGet(
            () -> {
              ModelYear modelYear = ModelYear.builder().value(year).build();
              return modelYearRepository.save(modelYear);
            });
  }

  private Set<Category> findCategories(Set<String> categoryNames) {
    Set<Category> categories = new HashSet<>();

    for (String categoryName : categoryNames) {
      Category category = findCategoryByName(categoryName);
      category.setModels(new HashSet<>());
      categories.add(category);
    }
    return categories;
  }

  private Category findCategoryByName(String categoryName) {
    return categoryRepository
        .findById(categoryName)
        .orElseGet(
            () -> {
              Category category = Category.builder().name(categoryName).build();
              return categoryRepository.save(category);
            });
  }
}
