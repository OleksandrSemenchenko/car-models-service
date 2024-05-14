package ua.foxminded.cars.service.imp;

import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.exceptionhandler.exceptions.YearNotFoundException;
import ua.foxminded.cars.repository.ModelYearRepository;
import ua.foxminded.cars.repository.entity.ModelYear;
import ua.foxminded.cars.service.ModelYearService;

@Service
@RequiredArgsConstructor
public class ModelYearServiceImp implements ModelYearService {

  private final ModelYearRepository modelYearRepository;

  @Override
  public void deleteYear(Year value) {
    if (!modelYearRepository.existsById(value)) {
      throw new YearNotFoundException(value);
    }
    modelYearRepository.deleteById(value);
  }

  @Override
  public void createYearIfNeeded(Year value) {
    if (!modelYearRepository.existsById(value)) {
      ModelYear modelYear = ModelYear.builder().value(value).build();
      modelYearRepository.saveAndFlush(modelYear);
    }
  }
}
