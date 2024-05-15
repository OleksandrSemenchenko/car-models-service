package ua.foxminded.cars.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.foxminded.cars.repository.ManufacturerRepository;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.service.ManufacturerService;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImp implements ManufacturerService {

  private final ManufacturerRepository manufacturerRepository;

  @Override
  public void deleteManufacturer(String manufacturerName) {
    if (!manufacturerRepository.existsById(manufacturerName)) {
      throw new ManufacturerNotFoundException(manufacturerName);
    }
    manufacturerRepository.deleteById(manufacturerName);
  }

  @Override
  public void createManufacturerIfNecessary(String manufacturerName) {
    if (!manufacturerRepository.existsById(manufacturerName)) {
      Manufacturer manufacturer = Manufacturer.builder().name(manufacturerName).build();
      manufacturerRepository.saveAndFlush(manufacturer);
    }
  }
}
