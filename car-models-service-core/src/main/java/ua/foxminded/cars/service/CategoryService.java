package ua.foxminded.cars.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import ua.foxminded.cars.service.dto.CategoryDto;

public interface CategoryService {

  boolean isCategoryExist(String name);

  List<CategoryDto> createCategories(Collection<CategoryDto> categories);

  List<CategoryDto> getCategories(Collection<String> categoryNames);

  void deleteCategory(String category);
}
