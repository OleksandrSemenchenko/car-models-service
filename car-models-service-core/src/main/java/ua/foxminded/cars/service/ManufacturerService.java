package ua.foxminded.cars.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.foxminded.cars.service.dto.ManufacturerDto;

public interface ManufacturerService {

  Page<ManufacturerDto> getAllManufacturers(Pageable pageable);

  ManufacturerDto getManufacturer(String name);

  void deleteManufacturer(String manufacturerName);

  boolean isManufacturerExistByName(String name);

  ManufacturerDto createManufacturer(ManufacturerDto manufacturerDto);
}
