package ua.com.foxminded.vehicles.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.entity.Vehicle;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, 
        uses = {CategoryMapper.class, ModelMapper.class, ManufacturerMapper.class})
public interface VehicleMapper {
    
    @Mapping(target = "year", source = "productionYear")
    VehicleDto map(Vehicle vehicle);
    
    @InheritInverseConfiguration
    Vehicle map(VehicleDto vehicleDto);
}
