package ua.foxminded.cars.service;

import java.util.List;
import java.util.Set;
import ua.foxminded.cars.service.dto.CategoryDto;

public interface CategoryService {

  List<CategoryDto> createCategoriesIfNecessary(List<String> categoryNames);

  List<CategoryDto> getCategories(Set<String> categoryNames);

  void deleteCategory(String category);
}
