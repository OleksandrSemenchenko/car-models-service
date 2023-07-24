package ua.com.foxminded.vehicles.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.vehicles.dto.ModelDto;
import ua.com.foxminded.vehicles.entity.Model;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ModelMapper {
    
    @Mapping(target = "vehicles", ignore = true)
    public ModelDto map(Model model);
    
    @InheritInverseConfiguration
    public Model map(ModelDto modelDto);
}
