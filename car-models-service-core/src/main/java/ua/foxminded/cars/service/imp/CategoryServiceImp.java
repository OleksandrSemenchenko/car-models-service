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
  public List<CategoryDto> getCategories(Set<String> categoryNames) {
    List<Category> categories = findAllCategoriesById(categoryNames);
    return categoryMapper.toDtoList(categories);
  }

  private List<Category> findAllCategoriesById(Set<String> categoryNames) {
    List<Category> receivedCategories = categoryRepository.findAllById(categoryNames);
    receivedCategories.forEach(
        category -> {
          if (!categoryNames.contains(category.getName())) {
            throw new CategoryNotFoundException(category.getName());
          }
        });
    return receivedCategories;
  }

  @Override
  public void deleteCategory(String categoryName) {
    if (!categoryRepository.existsById(categoryName)) {
      throw new CategoryNotFoundException(categoryName);
    }
    categoryRepository.deleteById(categoryName);
  }

  /**
   * Creates categories if it is not in the database and returns only created ones.
   *
   * @param categoryNames - category names
   * @return List<CategoryDto> - created categories
   */
  @Override
  public List<CategoryDto> createCategoriesIfNecessary(List<String> categoryNames) {
    List<String> shouldBeCreatedCategoryNames = defineCategoriesToCreate(categoryNames);
    List<Category> shouldBeCreatedCategories =
        shouldBeCreatedCategoryNames.stream()
            .map(name -> Category.builder().name(name).build())
            .toList();
    List<Category> createdCategories =
        categoryRepository.saveAllAndFlush(shouldBeCreatedCategories);
    return categoryMapper.toDtoList(createdCategories);
  }

  private List<String> defineCategoriesToCreate(List<String> categoryNames) {
    return categoryNames.stream()
        .filter(categoryName -> !categoryRepository.existsById(categoryName))
        .toList();
  }
}
