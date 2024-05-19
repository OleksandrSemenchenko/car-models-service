package ua.foxminded.cars.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.*;

import org.mapstruct.Mapper;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.service.dto.ManufacturerDto;

@Mapper(componentModel = SPRING)
public interface ManufacturerMapper {

  Manufacturer toEntity(ManufacturerDto manufacturerDto);

  ManufacturerDto toDto(Manufacturer manufacturer);
}
