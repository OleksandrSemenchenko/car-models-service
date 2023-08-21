package ua.com.foxminded.cars.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.cars.dto.ModelNameDto;
import ua.com.foxminded.cars.entity.ModelName;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModelNameMapper {
    
    ModelNameDto map(ModelName modelName);
    
    @InheritInverseConfiguration
    @Mapping(target = "models", ignore = true)
    ModelName map(ModelNameDto modelNameDto);
    
    default String modelToString(ModelName modelName) {
        return modelName.getName();
    }
    
    default ModelName stringToCategory(String name) {
        return ModelName.builder().name(name).build();
    }
}
