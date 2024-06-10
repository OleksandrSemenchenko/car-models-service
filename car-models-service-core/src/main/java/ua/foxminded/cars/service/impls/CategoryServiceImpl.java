package ua.foxminded.cars.service.impls;

import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.CATEGORY_ALREADY_EXISTS;
import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.CATEGORY_NOT_FOUND;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.config.PageSortConfig;
import ua.foxminded.cars.exceptionhandler.exceptions.CategoryAlreadyExistsException;
import ua.foxminded.cars.exceptionhandler.exceptions.CategoryNotFoundException;
import ua.foxminded.cars.mapper.CategoryMapper;
import ua.foxminded.cars.repository.CategoryRepository;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.service.AbstractService;
import ua.foxminded.cars.service.CategoryService;
import ua.foxminded.cars.service.dto.CategoryDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl extends AbstractService implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;
  private final PageSortConfig pageSortConfig;

  @Override
  public Page<CategoryDto> getAllCategories(Pageable pageable) {
    pageable =
        setDefaultSortIfNecessary(
            pageable,
            pageSortConfig.getCategorySortDirection(),
            pageSortConfig.getCategorySortBy());
    return categoryRepository.findAll(pageable).map(categoryMapper::toDto);
  }

  @Override
  public boolean isCategoryExist(String categoryName) {
    return categoryRepository.existsById(categoryName);
  }

  @Override
  public List<CategoryDto> getCategories(Collection<String> categoryNames) {
    List<Category> categories = findAllCategoriesByIds(categoryNames);
    return categoryMapper.toDtoList(categories);
  }

  private List<Category> findAllCategoriesByIds(Collection<String> categoryNames) {
    List<Category> foundCategories = categoryRepository.findAllById(categoryNames);
    verifyFoundCategories(categoryNames, foundCategories);
    return foundCategories;
  }

  private void verifyFoundCategories(
      Collection<String> expectedNames, List<Category> actualCategories) {
    List<String> messages =
        buildCategoryNotFoundExceptionMessagesIfNecessary(expectedNames, actualCategories);

    if (!messages.isEmpty()) {
      messages.forEach(log::debug);
      throw new CategoryNotFoundException(messages);
    }
  }

  private List<String> buildCategoryNotFoundExceptionMessagesIfNecessary(
      Collection<String> expectedNames, List<Category> actualCategories) {
    List<String> messages = new ArrayList<>();
    List<String> actualCategoryNames = getCategoryNames(actualCategories);

    for (String expectedName : expectedNames) {
      if (!actualCategoryNames.contains(expectedName)) {
        messages.add(CATEGORY_NOT_FOUND.formatted(expectedName));
      }
    }
    return messages;
  }

  @Override
  public void deleteCategory(String categoryName) {
    verifyIfCategoryExists(categoryName);
    categoryRepository.deleteById(categoryName);
  }

  private void verifyIfCategoryExists(String categoryName) {
    if (!categoryRepository.existsById(categoryName)) {
      log.debug(CATEGORY_NOT_FOUND.formatted(categoryName));
      throw new CategoryNotFoundException(categoryName);
    }
  }

  @Override
  public List<CategoryDto> createCategories(Collection<CategoryDto> categoryDtos) {
    List<Category> categories = categoryMapper.toEntityList(categoryDtos);
    verifyIfCategoriesAlreadyExist(categories);
    List<Category> createdCategories = categoryRepository.saveAllAndFlush(categories);
    return categoryMapper.toDtoList(createdCategories);
  }

  private void verifyIfCategoriesAlreadyExist(List<Category> categories) {
    List<String> categoryNames = getCategoryNames(categories);
    List<Category> foundCategories = categoryRepository.findAllById(categoryNames);

    if (!foundCategories.isEmpty()) {
      List<String> messages =
          foundCategories.stream()
              .map(category -> CATEGORY_ALREADY_EXISTS.formatted(category.getName()))
              .toList();
      messages.forEach(log::debug);
      throw new CategoryAlreadyExistsException(messages);
    }
  }

  private List<String> getCategoryNames(List<Category> categories) {
    return categories.stream().map(Category::getName).toList();
  }
}
