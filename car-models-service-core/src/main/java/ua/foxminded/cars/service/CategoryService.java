package ua.foxminded.cars.service;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.foxminded.cars.service.dto.CategoryDto;

public interface CategoryService {

  Page<CategoryDto> getAllCategories(Pageable pageable);

  boolean isCategoryExist(String name);

  List<CategoryDto> createCategories(Collection<CategoryDto> categories);

  List<CategoryDto> getCategories(Collection<String> categoryNames);

  void deleteCategory(String category);
}
