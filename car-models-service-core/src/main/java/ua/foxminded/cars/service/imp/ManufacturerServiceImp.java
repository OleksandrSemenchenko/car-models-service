package ua.foxminded.cars.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.exceptionhandler.exceptions.ManufacturerNotFoundException;
import ua.foxminded.cars.mapper.ManufacturerMapper;
import ua.foxminded.cars.repository.ManufacturerRepository;
import ua.foxminded.cars.repository.entity.Manufacturer;
import ua.foxminded.cars.service.ManufacturerService;
import ua.foxminded.cars.service.dto.ManufacturerDto;

@Service
@RequiredArgsConstructor
public class ManufacturerServiceImp implements ManufacturerService {

  private final ManufacturerRepository manufacturerRepository;
  private final ManufacturerMapper manufacturerMapper;

  @Override
  public void deleteManufacturer(String manufacturerName) {
    if (!manufacturerRepository.existsById(manufacturerName)) {
      throw new ManufacturerNotFoundException(manufacturerName);
    }
    manufacturerRepository.deleteById(manufacturerName);
  }

  @Override
  public boolean isManufacturerExistByName(String name) {
    return manufacturerRepository.existsById(name);
  }

  @Override
  public ManufacturerDto createManufacturer(ManufacturerDto manufacturerDto) {
    Manufacturer manufacturer = manufacturerMapper.toEntity(manufacturerDto);
    Manufacturer savedManufacturer = manufacturerRepository.saveAndFlush(manufacturer);
    return manufacturerMapper.toDto(savedManufacturer);
  }
}
