package ua.com.foxminded.mapper;

import ua.com.foxminded.repository.entity.Manufacturer;
import ua.com.foxminded.service.dto.ManufacturerDto;

public interface ManufacturerMapper {

  Manufacturer toEntity(ManufacturerDto manufacturerDto);
}
