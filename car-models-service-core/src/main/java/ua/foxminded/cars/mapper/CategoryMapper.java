package ua.foxminded.cars.mapper;

import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.service.dto.CategoryDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

  Set<CategoryDto> toDtoSet(List<Category> categories);

  CategoryDto toDto(Category category);

  @Mapping(target = "name", source = "category")
  Category stringToEntity(String category);

  default String entityToString(Category category) {
    return category.getName();
  }

  Set<String> categoriesToSet(Set<Category> categories);
}
