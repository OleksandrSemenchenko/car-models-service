package ua.com.foxminded.vehicles.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.entity.Manufacturer;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ManufacturerMapper {
    
    @Mapping(target = "vehicles", ignore = true)
    public ManufacturerDto map(Manufacturer manufacturer);
    
    @InheritInverseConfiguration
    public Manufacturer map(ManufacturerDto manufacturerDto);
}
