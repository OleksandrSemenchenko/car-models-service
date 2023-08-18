package ua.com.foxminded.vehicles.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModelMapper {
    
    ModelDto map(Model model);
    
    @InheritInverseConfiguration
    @Mapping(target = "vehicles", ignore = true)
    Model map(ModelDto modelDto);
    
    default String modelToString(Model model) {
        return model.getName();
    }
    
    default Model stringToCategory(String name) {
        return Model.builder().name(name).build();
    }
}
