package ua.foxminded.cars.mapper;

import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.service.dto.ManufacturerDto;

public interface ManufacturerMapper {

  Manufacturer toEntity(ManufacturerDto manufacturerDto);
}
