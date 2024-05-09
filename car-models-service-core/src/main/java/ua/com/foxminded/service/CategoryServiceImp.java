package ua.com.foxminded.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.foxminded.exceptionhandler.exceptions.CategoryNotFoundException;
import ua.com.foxminded.mapper.CategoryMapper;
import ua.com.foxminded.repository.CategoryRepository;
import ua.com.foxminded.repository.entity.Category;
import ua.com.foxminded.service.dto.CategoryDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  private void verifyIfAllCategoriesPresent(Set<String> expectedNames, List<Category> actualCategories) {
    actualCategories.forEach(category -> {
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
  public void createCategoryIfNeeded(String categoryName) {
    if (!categoryRepository.existsById(categoryName)) {
      Category category = Category.builder().name(categoryName).build();
      categoryRepository.save(category);
    }
  }
}
