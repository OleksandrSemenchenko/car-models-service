package ua.foxminded.cars.service.imp;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ua.foxminded.cars.config.AppConfig;
import ua.foxminded.cars.exceptionhandler.exceptions.ExceptionMessages;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelAlreadyExistsException;
import ua.foxminded.cars.exceptionhandler.exceptions.ModelNotFoundException;
import ua.foxminded.cars.exceptionhandler.exceptions.PeriodNotValidException;
import ua.foxminded.cars.mapper.ModelMapper;
import ua.foxminded.cars.repository.ModelRepository;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.repository.entity.Model;
import ua.foxminded.cars.repository.entity.ModelYear;
import ua.foxminded.cars.repository.specification.ModelSpecification;
import ua.foxminded.cars.repository.specification.SearchFilter;
import ua.foxminded.cars.service.CategoryService;
import ua.foxminded.cars.service.ManufacturerService;
import ua.foxminded.cars.service.ModelService;
import ua.foxminded.cars.service.ModelYearService;
import ua.foxminded.cars.service.dto.CategoryDto;
import ua.foxminded.cars.service.dto.ManufacturerDto;
import ua.foxminded.cars.service.dto.ModelDto;
import ua.foxminded.cars.service.dto.ModelYearDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelServiceImp implements ModelService {

  private static final String SEARCH_MODELS_CACHE = "searchModels";
  private static final String GET_MODEL_BY_ID_CACHE = "getModelById";
  private static final String GET_MODEL_CACHE = "getModel";

  private final ModelRepository modelRepository;
  private final ModelMapper modelMapper;
  private final AppConfig appConfig;
  private final ManufacturerService manufacturerService;
  private final ModelYearService modelYearService;
  private final CategoryService categoryService;

  @Override
  public boolean isModelExistById(UUID modelId) {
    return modelRepository.existsById(modelId);
  }

  /**
   * Updates a model, if related entities after the updating have no relations it removes them too.
   *
   * @param targetModelDto - the state of a model that should be in a database
   * @return - a model object that reflects a database state after updating
   */
  @Override
  @Transactional
  @Caching(
      evict = @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
      put = {
        @CachePut(
            value = GET_MODEL_CACHE,
            key =
                "{ 'getModel', #targetModelDto.manufacturer, #targetModelDto.name, #targetModelDto.year }"),
        @CachePut(value = GET_MODEL_BY_ID_CACHE, key = "{ 'getModelById', #targetModelDto.id }")
      })
  public ModelDto updateModel(ModelDto targetModelDto) {
    Model sourceModel =
        findModelBySpecification(
            targetModelDto.getManufacturer(), targetModelDto.getName(), targetModelDto.getYear());

    List<String> sourceCategories = getCategoryNames(sourceModel.getCategories());
    List<String> targetCategories = targetModelDto.getCategories();
    List<CategoryDto> shouldBeAssignedCategories = selectCategoriesToAssign(sourceCategories, targetCategories);
    List<CategoryDto> persistedCategories = categoryService.createCategories(shouldBeAssignedCategories);
    putModelToCategories(sourceModel.getId(), persistedCategories);
    sourceCategories.removeAll(targetCategories);

    if (!sourceCategories.isEmpty()) {
      removeModelFromCategories(sourceModel.getId(), sourceCategories);
    }
    targetModelDto.setId(sourceModel.getId());
    return targetModelDto;
  }

  private List<CategoryDto> selectCategoriesToAssign(Collection<String> sourceCategories,
                                                     Collection<String> targetCategories) {
    return targetCategories.stream()
      .filter(category -> !sourceCategories.contains(category))
      .map(category -> CategoryDto.builder().name(category).build())
      .toList();
  }

  private List<String> getCategoryNames(Collection<Category> categories) {
    return categories.stream().map(Category::getName).toList();
  }

  private void removeModelFromCategories(UUID modelId, Collection<String> categoryNames) {
    for (String categoryName : categoryNames) {
      modelRepository.removeModelFromCategory(modelId, categoryName);
      deleteCategoryIfNecessary(categoryName);
    }
  }

  /**
   * Deletes a model and deletes related entities if they have no relations.
   *
   * @param modelId - a model ID
   */
  @Override
  @Transactional
  @Caching(
      evict = {
        @CacheEvict(value = SEARCH_MODELS_CACHE, allEntries = true),
        @CacheEvict(value = GET_MODEL_BY_ID_CACHE, key = "{ 'getModelById', #modelId }"),
        @CacheEvict(value = GET_MODEL_CACHE, allEntries = true)
      })
  public void deleteModelById(UUID modelId) {
    Model model =
        modelRepository.findById(modelId).orElseThrow(() -> new ModelNotFoundException(modelId));
    modelRepository.delete(model);
    deleteManufacturerIfNecessary(model.getManufacturer());
    deleteModelYearIfNecessary(model.getYear());
    deleteCategoriesIfNecessary(model.getCategories());
  }

  private void deleteManufacturerIfNecessary(Manufacturer manufacturer) {
    if (!modelRepository.existsByManufacturerName(manufacturer.getName())) {
      manufacturerService.deleteManufacturer(manufacturer.getName());
    }
  }

  private void deleteModelYearIfNecessary(ModelYear modelYear) {
    if (!modelRepository.existsByYearValue(modelYear.getYear())) {
      modelYearService.deleteYear(modelYear.getYear().getValue());
    }
  }

  private void deleteCategoriesIfNecessary(Set<Category> categories) {
    List<String> categoryNames = categories.stream()
      .map(Category::getName)
      .toList();

    for (String categoryName : categoryNames) {
      deleteCategoryIfNecessary(categoryName);
    }
  }

  private void deleteCategoryIfNecessary(String categoryName) {
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

  /**
   * Searches for models by provided parameters if no parameters are present it returns all models
   * in a database. There is a validation, a value of the maxYear must be greater than a value of minYear one.
   *
   * @param searchFilter - parameters for the search
   * @param pageable - parameter for a page
   * @return Page<ModelDto> - a page containing models
   */
  @Override
  @Cacheable(value = SEARCH_MODELS_CACHE, key = "{ #root.methodName, #searchFilter, #pageable }")
  public Page<ModelDto> searchModel(SearchFilter searchFilter, Pageable pageable) {
    SearchFilter validatedSearchFilter = validateSearchFilter(searchFilter);
    Specification<Model> specification = ModelSpecification.getSpecification(validatedSearchFilter);
    pageable = setDefaultSortIfNecessary(pageable);
    return modelRepository.findAll(specification, pageable).map(modelMapper::toDto);
  }

  private SearchFilter validateSearchFilter(SearchFilter searchFilter) {
    Integer minYear = searchFilter.getMinYear();
    Integer maxYear = searchFilter.getMaxYear();

    if (Objects.nonNull(minYear) && Objects.nonNull(maxYear) && minYear > maxYear) {
      throw new PeriodNotValidException(minYear, maxYear);
    } else {
      return searchFilter;
    }
  }

  private Pageable setDefaultSortIfNecessary(Pageable pageRequest) {
    if (pageRequest.getSort().isUnsorted()) {
      Sort defaulSort = Sort.by(appConfig.getModelSortDirection(), appConfig.getModelSortBy());
      return PageRequest.of(
          pageRequest.getPageNumber(),
          pageRequest.getPageSize(),
          pageRequest.getSortOr(defaulSort));
    }
    return pageRequest;
  }

  /**
   * Creates a model. If a database has no necessary relations then they will be created.
   *
   * @param modelDto - DTO of a model
   * @return ModelDto
   */
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
    createManufacturerIfNecessary(modelDto.getManufacturer());
    createModelYearIfNecessary(modelDto.getYear());
    ModelDto savedModel = saveModel(modelDto);
    List<CategoryDto> persistedCategories = createCategoriesIfNecessary(modelDto.getCategories());
    putModelToCategories(savedModel.getId(), persistedCategories);
    modelDto.setId(savedModel.getId());
    return modelDto;
  }

  private void verifyIfModelExists(String manufacturerName, String modelName, int year) {
    Specification<Model> specification = buildSpecification(manufacturerName, modelName, year);
    modelRepository
      .findOne(specification)
      .ifPresent(
        entity -> {
          log.debug(ExceptionMessages.MODEL_ALREADY_EXIST_BY_PARAMETERS
            .formatted(manufacturerName, modelName, year, entity.getId()));
          throw new ModelAlreadyExistsException(
            manufacturerName, modelName, year, entity.getId());
        });
  }

  private Specification<Model> buildSpecification(String manufacturer, String name, int year) {
    SearchFilter searchFilter =
      SearchFilter.builder().manufacturer(manufacturer).name(name).year(year).build();
    return ModelSpecification.getSpecification(searchFilter);
  }

  private void createManufacturerIfNecessary(String manufacturerName) {
    if (!manufacturerService.isManufacturerExistByName(manufacturerName)) {
      ManufacturerDto manufacturerDto = ManufacturerDto.builder()
        .name(manufacturerName)
        .build();
      manufacturerService.createManufacturer(manufacturerDto);
    }
  }

  private List<CategoryDto> defineCategoriesToCreate(List<String> categoryNames) {
    return categoryNames.stream()
      .filter(categoryName -> !categoryService.isCategoryExist(categoryName))
      .map(categoryName -> CategoryDto.builder().name(categoryName).build())
      .toList();
  }

  private void createModelYearIfNecessary(int year) {
    if (!modelYearService.isModelYearExist(year)) {
      ModelYearDto modelYearDto = ModelYearDto.builder()
        .year(year)
        .build();
      modelYearService.createModelYear(modelYearDto);
    }
  }

  private ModelDto saveModel(ModelDto modelDto) {
    Model model = modelMapper.toEntity(modelDto);
    Model createdModel = modelRepository.save(model);
    return modelMapper.toDto(createdModel);
  }

  private List<CategoryDto> createCategoriesIfNecessary(List<String> categories) {
    List<CategoryDto> shouldBeCreatedCategories = defineCategoriesToCreate(categories);
    categoryService.createCategories(shouldBeCreatedCategories);
    return categoryService.getCategories(categories);
  }

  private void putModelToCategories(UUID modelId, List<CategoryDto> categories) {
    for (CategoryDto category : categories) {
      modelRepository.putModelToCategory(modelId, category.getName());
    }
  }
}
