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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ModelServiceImp implements ModelService {

  private final ModelRepository modelRepository;
  private final CategoryRepository categoryRepository;
  private final ManufacturerRepository manufacturerRepository;
  private final ModelYearRepository modelYearRepository;
  private final ModelMapper modelMapper;

  @Override
  @Transactional
  public ModelDto updateModel(ModelDto modelDto) {
    Model model = modelRepository
      .findById(modelDto.getId())
      .orElseThrow(() -> new ModelNotFoundException(modelDto.getId()));
    Model modelWithRelations = defineRelationships(model, modelDto);
    Model savedModel = modelRepository.save(modelWithRelations);
    return modelMapper.toDto(savedModel);
  }

  //TODO delete categories years and manufactures also
  @Override
  @Transactional
  public void deleteModelById(String id) {
    modelRepository
      .findById(id)
      .orElseThrow(() -> new ModelNotFoundException(id));
    modelRepository.deleteById(id);
  }

  @Override
  public ModelDto getModelById(String modelId) {
    Model model = modelRepository.findById(modelId).orElseThrow(() -> new ModelNotFoundException(modelId));
    return modelMapper.toDto(model);
  }

  @Override
  public ModelDto getModel(String manufacturer, String name, int year) {
    Specification<Model> specification = buildSpecification(manufacturer, name, year);

    return modelRepository
      .findOne(specification)
      .map(modelMapper::toDto)
      .orElseThrow(() -> new ModelNotFoundException(manufacturer, name, year));
  }

  private Specification<Model> buildSpecification(String manufacturer, String name, int year) {
    SearchFilter searchFilter = SearchFilter.builder()
      .manufacturer(manufacturer)
      .name(name)
      .year(year).build();
    return ModelSpecification.getSpecification(searchFilter);
  }

  //TODO sorting
  //TODO cache
  @Override
  public Page<ModelDto> search(SearchFilter searchFilter, Pageable pageRequest) {
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
    return modelRepository.findAll(specification, pageRequest).map(modelMapper::toDto);
  }

  @Override
  @Transactional
  public ModelDto create(ModelDto modelDto) {
    verifyIfModelExists(modelDto.getManufacturer(), modelDto.getName(), modelDto.getYear());
    Model model = modelMapper.toEntity(modelDto);
    Model modelWithRelations = defineRelationships(model, modelDto);
    Model savedModel = modelRepository.save(modelWithRelations);
    return modelMapper.toDto(savedModel);
  }

  private Model defineRelationships(Model model, ModelDto modelDto) {
    Manufacturer manufacturer = findManufacturerByName(modelDto.getManufacturer());
    ModelYear year = findModelYearByValue(modelDto.getYear());
    Set<Category> categories = findCategories(modelDto.getCategories());
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
    return manufacturerRepository.findById(manufacturerName).orElseGet(() -> {
      Manufacturer manufacturer = Manufacturer.builder().name(manufacturerName).build();
      return manufacturerRepository.save(manufacturer);
    });
  }

  private ModelYear findModelYearByValue(int year) {
    return modelYearRepository.findById(year).orElseGet(() -> {
      ModelYear modelYear = ModelYear.builder().value(year).build();
      return modelYearRepository.save(modelYear);
    });
  }

  private Set<Category> findCategories(Set<String> categoryNames) {
    Set<Category> categories = new HashSet<>();

    for (String categoryName : categoryNames) {
      Category category = findCategoryByName(categoryName);
      categories.add(category);
    }
    return categories;
  }

  private Category findCategoryByName(String categoryName) {
    return categoryRepository.findById(categoryName).orElseGet(() -> {
      Category category = Category.builder().name(categoryName).build();
      return categoryRepository.save(category);
    });
  }
}
