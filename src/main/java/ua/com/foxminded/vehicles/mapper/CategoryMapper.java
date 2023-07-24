package ua.com.foxminded.vehicles.mapper;

import java.util.Set;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import ua.com.foxminded.vehicles.dto.CategoryDto;
import ua.com.foxminded.vehicles.entity.Category;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    
    @Named("categoriesDtoWithoutRelationships")
    @IterableMapping(qualifiedByName = "categoryDtoWithoutRelationships")
    public Set<CategoryDto>  mapWhithoutRelationships(Set<Category> categories);
    
    @Named("categoryDtoWithoutRelationships")
    @Mapping(target = "vehicles", ignore = true)
    public CategoryDto map(Category category);
    
    @InheritInverseConfiguration
    public Category map(CategoryDto categoryDto);
}
