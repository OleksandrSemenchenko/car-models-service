package ua.foxminded.cars.service;

import java.util.Set;
import ua.foxminded.cars.service.dto.CategoryDto;

public interface CategoryService {

  Set<CategoryDto> getCategories(Set<String> categoryNames);

  void deleteCategory(String category);

  void createCategoryIfNecessary(String category);
}
