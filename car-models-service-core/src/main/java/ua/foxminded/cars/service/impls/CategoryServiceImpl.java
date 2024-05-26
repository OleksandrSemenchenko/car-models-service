package ua.foxminded.cars.service.impls;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.exceptionhandler.exceptions.CategoryNotFoundException;
import ua.foxminded.cars.mapper.CategoryMapper;
import ua.foxminded.cars.repository.CategoryRepository;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.service.CategoryService;
import ua.foxminded.cars.service.dto.CategoryDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Override
  public boolean isCategoryExist(String categoryName) {
    return categoryRepository.existsById(categoryName);
  }

  @Override
  public List<CategoryDto> getCategories(Collection<String> categoryNames) {
    List<Category> categories = findAllCategoriesById(categoryNames);
    return categoryMapper.toDtoList(categories);
  }

  private List<Category> findAllCategoriesById(Collection<String> categoryNames) {
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

  @Override
  public List<CategoryDto> createCategories(Collection<CategoryDto> categoriesDto) {
    List<Category> categories = categoryMapper.toEntityList(categoriesDto);
    List<Category> createdCategories = categoryRepository.saveAllAndFlush(categories);
    return categoryMapper.toDtoList(createdCategories);
  }
}
