package ua.com.foxminded.cars.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.cars.dto.ManufacturerDto;
import ua.com.foxminded.cars.entity.Manufacturer;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ManufacturerMapper {
    
    ManufacturerDto map(Manufacturer manufacturer);
    
    @InheritInverseConfiguration
    @Mapping(target = "models", ignore = true)
    Manufacturer map(ManufacturerDto manufacturerDto);
    
    default String manufacturerToString(Manufacturer manufacturer) {
        return manufacturer.getName();
    }
    
    default Manufacturer stringToManufacturer(String name) {
        return Manufacturer.builder().name(name).build();
    }
}
