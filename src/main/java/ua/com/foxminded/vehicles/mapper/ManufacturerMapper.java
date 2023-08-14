package ua.com.foxminded.vehicles.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.vehicles.dto.ManufacturerDto;
import ua.com.foxminded.vehicles.entity.Manufacturer;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ManufacturerMapper {
    
    ManufacturerDto map(Manufacturer manufacturer);
    
    @InheritInverseConfiguration
    @Mapping(target = "vehicles", ignore = true)
    Manufacturer map(ManufacturerDto manufacturerDto);
    
    default String asString(Manufacturer manufacturer) {
        return manufacturer.getName();
    }
    
    default Manufacturer stringToManufacturer(String name) {
        return Manufacturer.builder().name(name).build();
    }
}
