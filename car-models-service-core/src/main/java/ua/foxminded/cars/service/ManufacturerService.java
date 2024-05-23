package ua.foxminded.cars.service;

import ua.foxminded.cars.service.dto.ManufacturerDto;

public interface ManufacturerService {

  void deleteManufacturer(String manufacturerName);

  boolean isManufacturerExistByName(String name);

  ManufacturerDto createManufacturer(ManufacturerDto manufacturerDto);
}
