package ua.com.foxminded.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.foxminded.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.com.foxminded.repository.ManufacturerRepository;
import ua.com.foxminded.repository.entity.Manufacturer;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImp implements ManufacturerService {

  private final ManufacturerRepository manufacturerRepository;

  @Override
  public void deleteManufacturer(String manufacturerName) {
    if (!manufacturerRepository.existsById(manufacturerName)) {
      throw new ManufacturerNotFoundException(manufacturerName);
    }
  }

  @Override
  public void createManufacturerIfNeeded(String manufacturerName) {
    if (!manufacturerRepository.existsById(manufacturerName)) {
      Manufacturer manufacturer = Manufacturer.builder().name(manufacturerName).build();
      manufacturerRepository.save(manufacturer);
    }
  }
}
