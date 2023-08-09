package ua.com.foxminded.vehicles.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import ua.com.foxminded.vehicles.dto.VehicleDto;
import ua.com.foxminded.vehicles.entity.Vehicle;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = CategoryMapper.class)
public interface VehicleMapper {
    
    @Mapping(target = "categories", qualifiedByName = "categoriesDtoWithoutRelationships")
    @Mapping(target = "model.vehicles", ignore = true)
    @Mapping(target = "manufacturer.vehicles", ignore = true)
    VehicleDto map(Vehicle vehicle);
    Vehicle map(VehicleDto vehicleDto);
}
