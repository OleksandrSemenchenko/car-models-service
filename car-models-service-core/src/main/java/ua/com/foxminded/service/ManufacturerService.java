package ua.com.foxminded.service;

import ua.com.foxminded.repository.entity.Manufacturer;
import ua.com.foxminded.service.dto.ManufacturerDto;

public interface ManufacturerService {

  void deleteManufacturer(String manufacturerName);

  void createManufacturerIfNeeded(String manufacturerName);
}
