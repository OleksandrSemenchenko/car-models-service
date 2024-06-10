package ua.foxminded.cars.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.foxminded.cars.repository.entity.Category;
import ua.foxminded.cars.service.dto.CategoryDto;

@Mapper(componentModel = SPRING)
public interface CategoryMapper {

  List<Category> toEntityList(Collection<CategoryDto> categoriesDto);

  List<CategoryDto> toDtoList(Collection<Category> categories);

  CategoryDto toDto(Category category);

  @Mapping(target = "name", source = "category")
  Category stringToEntity(String category);

  default String entityToString(Category category) {
    return category.getName();
  }
}
