package ua.com.foxminded.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.dto.ModelNameDto;
import ua.com.foxminded.entity.ModelName;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModelNameMapper {
    
    ModelNameDto map(ModelName modelName);
    
    @InheritInverseConfiguration
    @Mapping(target = "models", ignore = true)
    ModelName map(ModelNameDto modelNameDto);
    
    default String modelNameToString(ModelName modelName) {
        return modelName.getName();
    }
    
    default ModelName stringToModelName(String name) {
        return ModelName.builder().name(name).build();
    }
}
