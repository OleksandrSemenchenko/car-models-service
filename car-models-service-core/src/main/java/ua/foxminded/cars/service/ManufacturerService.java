package ua.foxminded.cars.service;

public interface ManufacturerService {

  void deleteManufacturer(String manufacturerName);

  void createManufacturerIfNecessary(String manufacturerName);
}
