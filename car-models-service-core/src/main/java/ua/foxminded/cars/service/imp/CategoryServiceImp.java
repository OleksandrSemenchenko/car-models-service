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
  public void createCategoryIfNecessary(String categoryName) {
    if (!categoryRepository.existsById(categoryName)) {
      Category category = Category.builder().name(categoryName).build();
      categoryRepository.saveAndFlush(category);
    }
  }
}
