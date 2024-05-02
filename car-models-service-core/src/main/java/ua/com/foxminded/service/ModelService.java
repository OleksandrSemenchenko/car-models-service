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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.com.foxminded.exceptionhandler.exceptions.CategoryNotFoundException;
import ua.com.foxminded.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.com.foxminded.exceptionhandler.exceptions.ModelAlreadyExistsException;
import ua.com.foxminded.exceptionhandler.exceptions.ModelNameNotFoundException;
import ua.com.foxminded.exceptionhandler.exceptions.ModelNotFoundException;
import ua.com.foxminded.exceptionhandler.exceptions.NotFoundException;
import ua.com.foxminded.mapper.ModelMapper;
import ua.com.foxminded.repository.CategoryRepository;
import ua.com.foxminded.repository.ManufacturerRepository;
import ua.com.foxminded.repository.ModelNameRepository;
import ua.com.foxminded.repository.ModelRepository;
import ua.com.foxminded.repository.entity.Category;
import ua.com.foxminded.repository.entity.Model;
import ua.com.foxminded.service.dto.CategoryDto;
import ua.com.foxminded.service.dto.ModelDto;
import ua.com.foxminded.specification.ModelSpecification;
import ua.com.foxminded.specification.SearchFilter;

@Service
@RequiredArgsConstructor
public class ModelService {

  public static final String NO_MODEL_WITH_SUCH_ID = "The model with id=%s doesn't exist";

  private final ModelRepository modelRepository;
  private final CategoryRepository categoryRepository;
  private final ModelNameRepository modelNameRepository;
  private final ManufacturerRepository manufacturerRepository;
  private final ModelMapper modelMapper;

  public Page<ModelDto> search(SearchFilter searchFilter, Pageable pageRequest) {
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
    return modelRepository.findAll(specification, pageRequest).map(modelMapper::map);
  }

  @Transactional
  public ModelDto create(ModelDto ModelDto) {
    verifyIfModelExists(ModelDto.getManufacturer(), ModelDto.getName(), ModelDto.getYear());

    Model model = Model.builder().year(ModelDto.getYear())
                                 .categories(new HashSet<>()).build();

    Model modelWithUpdatedCategories = updateCategoryRelations(ModelDto, model);
    
    //TODO
    updateManufacturerRelation(ModelDto, modelWithUpdatedCategories);
    updateModelRelation(ModelDto, model);

    var persistedVehicle = modelRepository.save(model);
    return modelMapper.map(persistedVehicle);
  }
  
  private void verifyIfModelExists(String manufacturer, String model, int year) {
    Specification<Model> specification = buildSpecification(manufacturer, model, year);

    modelRepository.findOne(specification).ifPresent(entity -> {
      throw new ModelAlreadyExistsException(entity.getId());
    });
  }
  
  private Model updateCategoryRelations(ModelDto modelDto, Model model) {
    
    Model cleanModel = clearEntityFromUnnecessaryCategories(modelDto.getCategories(), model);
    return addAbsentCategoriesToEntity(modelDto.getCategories(), cleanModel);
  }
  
  private Model clearEntityFromUnnecessaryCategories(Set<String> necessaryCategoryNames, Model model) {
    List<Category> unnecessaryCategories = model.getCategories().stream()
        .filter(category -> !necessaryCategoryNames.contains(category.getName()))
        .toList();
    

    for (Category category : unnecessaryCategories) {
      model.removeCategory(category);
    }
    return model;
  }
  
  private Model addAbsentCategoriesToEntity(Set<String> necessaryCategoryNames, Model model) {
    Set<Category> categories = model.getCategories();
    Set<CategoryDto> absentCategories = necessaryCategoryNames.stream()
        .filter(categoryName -> isNotPresentCategory(categoryName, categories))
        .map(categoryName -> CategoryDto.builder().name(categoryName).build())
        .collect(Collectors.toSet());
    return addCategoryRelations(absentCategories, model);
  }
  
  private boolean isNotPresentCategory(String categoryName, Set<Category> categories) {
    return categories.stream().noneMatch(category -> category.getName().equals(categoryName));
  }
  
  public ModelDto getModel(String manufacturer, String name, int year) {
    Specification<Model> specification = buildSpecification(manufacturer, name, year);

    return modelRepository
        .findOne(specification)
        .map(modelMapper::map)
        .orElseThrow(() -> new ModelNotFoundException(manufacturer, name, year));
  }
  
  private Specification<Model>  buildSpecification(String manufacturer, String name, int year) {
    SearchFilter searchFilter = SearchFilter.builder()
        .manufacturer(manufacturer)
        .model(name)
        .year(year).build();
     return ModelSpecification.getSpecification(searchFilter);
  }

  
  

  @Transactional
  public ModelDto update(ModelDto modelDto) {
    SearchFilter searchFilter =
        SearchFilter.builder()
            .manufacturer(modelDto.getManufacturer())
            .model(modelDto.getName())
            .year(modelDto.getYear())
            .build();
    Specification<Model> specification = ModelSpecification.getSpecification(searchFilter);
    var model = modelRepository.findOne(specification)
                               .orElseThrow(() -> new ModelNotFoundException(modelDto.getManufacturer(), 
                                                                             modelDto.getName(), 
                                                                             modelDto.getYear()));

    updateCategoryRelations(modelDto, model);

    var updatedModel = modelRepository.save(model);
    return modelMapper.map(updatedModel);
  }

  @Transactional
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

  private Model addCategoryRelations(Set<CategoryDto> categoriesDto, Model model) {
    for (CategoryDto categoryDto : categoriesDto) {
      var categoryName = categoryDto.getName();
      var category = categoryRepository.findById(categoryName)
          .orElseThrow(() -> new CategoryNotFoundException(categoryName));
      model.addCategory(category);
    }
    return model;
  }

  private void updateModelRelation(ModelDto modelDto, Model model) {
    if (modelDto.getName() != null) {
      var name = modelDto.getName();
      var modelName =
          modelNameRepository
              .findById(name)
              .orElseThrow(() -> new ModelNameNotFoundException(name));
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
                  () -> new ManufacturerNotFoundException(manufacturerName));
      model.setManufacturer(manufacturer);
    } else {
      model.setManufacturer(null);
    }
  }
}
