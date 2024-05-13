package ua.foxminded.cars.service;

public interface ManufacturerService {

  void deleteManufacturer(String manufacturerName);

  void createManufacturerIfNeeded(String manufacturerName);
}
