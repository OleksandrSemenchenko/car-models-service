package ua.com.foxminded.service;

import ua.com.foxminded.service.dto.CategoryDto;

import java.util.List;
import java.util.Set;

public interface CategoryService {

  Set<CategoryDto> getCategories(Set<String> categoryNames);

  void deleteCategory(String category);

  void createCategoryIfNeeded(String category);
}
