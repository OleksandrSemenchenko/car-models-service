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

import static ua.com.foxminded.service.CategoryService.NO_CATEGORY;
import static ua.com.foxminded.service.ManufacturerService.NO_MANUFACTURER;
import static ua.com.foxminded.service.ModelNameService.NO_MODEL_NAME;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.dto.CategoryDto;
import ua.com.foxminded.dto.ModelDto;
import ua.com.foxminded.entity.Category;
import ua.com.foxminded.entity.Model;
import ua.com.foxminded.exception.AlreadyExistsException;
import ua.com.foxminded.exception.NotFoundException;
import ua.com.foxminded.mapper.ModelMapper;
import ua.com.foxminded.repository.CategoryRepository;
import ua.com.foxminded.repository.ManufacturerRepository;
import ua.com.foxminded.repository.ModelNameRepository;
import ua.com.foxminded.repository.ModelRepository;
import ua.com.foxminded.specification.ModelSpecification;
import ua.com.foxminded.specification.SearchFilter;

@Service
@Transactional
@RequiredArgsConstructor
public class ModelService {

  public static final String NO_SUCH_MODEL =
      "The model with manufacturer '%s', name '%s' and year '%s' doesn't exist";
  public static final String NO_MODEL_WITH_SUCH_ID = "The model with id=%s doesn't exist";
  public static final String MODEL_ALREADY_EXISTS = "Such model with id='%s' already exists";

  private final ModelRepository modelRepository;
  private final CategoryRepository categoryRepository;
  private final ModelNameRepository modelNameRepository;
  private final ManufacturerRepository manufacturerRepository;
  private final ModelMapper modelMapper;

  public ModelDto getByManufacturerAndNameAndYear(String manufacturer, String name, int year) {
    SearchFilter searchFilter =
        SearchFilter.builder().manufacturer(manufacturer).model(name).year(year).build();
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);

    return modelRepository
        .findOne(specification)
        .map(modelMapper::map)
        .orElseThrow(
            () -> new NotFoundException(String.format(NO_SUCH_MODEL, manufacturer, name, year)));
  }

  public Page<ModelDto> search(SearchFilter searchFilter, Pageable pageRequest) {
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
    return modelRepository.findAll(specification, pageRequest).map(modelMapper::map);
  }

  public ModelDto create(ModelDto modelDto) {
    throwIfPresentByManufacturerAndModelAndYear(
        modelDto.getManufacturer(), modelDto.getName(), modelDto.getYear());

    var model = Model.builder().year(modelDto.getYear()).categories(new HashSet<>()).build();

    updateCategoryRelations(modelDto, model);
    updateManufacturerRelation(modelDto, model);
    updateModelRelation(modelDto, model);

    var persistedVehicle = modelRepository.save(model);
    return modelMapper.map(persistedVehicle);
  }

  public ModelDto update(ModelDto modelDto) {
    SearchFilter searchFilter =
        SearchFilter.builder()
            .manufacturer(modelDto.getManufacturer())
            .model(modelDto.getName())
            .year(modelDto.getYear())
            .build();
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
    var model =
        modelRepository
            .findOne(specification)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        String.format(
                            NO_SUCH_MODEL,
                            modelDto.getManufacturer(),
                            modelDto.getName(),
                            modelDto.getYear())));

    updateCategoryRelations(modelDto, model);

    var updatedModel = modelRepository.save(model);
    return modelMapper.map(updatedModel);
  }

  public void deleteById(String id) {
    modelRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(String.format(NO_MODEL_WITH_SUCH_ID, id)));
    modelRepository.deleteById(id);
  }

  public ModelDto getById(String id) {
    return modelRepository
        .findById(id)
        .map(modelMapper::map)
        .orElseThrow(() -> new NotFoundException(String.format(NO_MODEL_WITH_SUCH_ID, id)));
  }

  private void throwIfPresentByManufacturerAndModelAndYear(
      String manufacturer, String model, int year) {
    SearchFilter searchFilter =
        SearchFilter.builder().manufacturer(manufacturer).model(model).year(year).build();
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
    Optional<Model> vehicleOptional = modelRepository.findOne(specification);

    if (vehicleOptional.isPresent()) {
      throw new AlreadyExistsException(
          String.format(MODEL_ALREADY_EXISTS, vehicleOptional.get().getId()));
    }
  }

  private void updateCategoryRelations(ModelDto modelDto, Model model) {
    List<Category> unnecessaryCategories =
        model.getCategories().stream()
            .filter(
                category -> {
                  return modelDto.getCategories().stream()
                      .noneMatch(categoryName -> category.getName().equals(categoryName));
                })
            .toList();

    for (Category category : unnecessaryCategories) {
      model.removeCategory(category);
    }

    Set<CategoryDto> necessaryCategories =
        modelDto.getCategories().stream()
            .filter(
                categoryDto -> {
                  return model.getCategories().stream()
                      .noneMatch(category -> categoryDto.equals(category.getName()));
                })
            .map(categoryName -> CategoryDto.builder().name(categoryName).build())
            .collect(Collectors.toSet());

    addCategoryRelations(necessaryCategories, model);
  }

  private void addCategoryRelations(Set<CategoryDto> categoriesDto, Model model) {
    for (CategoryDto categoryDto : categoriesDto) {
      var categoryName = categoryDto.getName();
      var category =
          categoryRepository
              .findById(categoryName)
              .orElseThrow(() -> new NotFoundException(String.format(NO_CATEGORY, categoryName)));
      model.addCategory(category);
    }
  }

  private void updateModelRelation(ModelDto modelDto, Model model) {
    if (modelDto.getName() != null) {
      var name = modelDto.getName();
      var modelName =
          modelNameRepository
              .findById(name)
              .orElseThrow(() -> new NotFoundException(String.format(NO_MODEL_NAME, name)));
      model.setModelName(modelName);
    } else {
      model.setModelName(null);
    }
  }

  private void updateManufacturerRelation(ModelDto modelDto, Model model) {
    if (modelDto.getManufacturer() != null) {
      var manufacturerName = modelDto.getManufacturer();
      var manufacturer =
          manufacturerRepository
              .findById(manufacturerName)
              .orElseThrow(
                  () -> new NotFoundException(String.format(NO_MANUFACTURER, manufacturerName)));
      model.setManufacturer(manufacturer);
    } else {
      model.setManufacturer(null);
    }
  }
}
