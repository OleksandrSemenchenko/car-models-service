package ua.com.foxminded.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.com.foxminded.dto.CategoryDto;
import ua.com.foxminded.entity.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    
    CategoryDto map(Category category);
    
    @InheritInverseConfiguration
    @Mapping(target = "models", ignore = true)
    Category map(CategoryDto categoryDto);
    
    default String categoryToString(Category category) {
        return category.getName();
    }
    
    default Category stringToCategory(String name) {
        return Category.builder().name(name).build();
    }
}
