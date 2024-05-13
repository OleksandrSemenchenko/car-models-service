package ua.foxminded.cars.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.exceptionhandler.exceptions.YearNotFoundException;
import ua.foxminded.cars.repository.YearRepository;
import ua.foxminded.cars.repository.entity.Year;
import ua.foxminded.cars.service.YearService;

@Service
@RequiredArgsConstructor
public class YearServiceImp implements YearService {

  private final YearRepository yearRepository;

  @Override
  public void deleteYear(int value) {
    if (!yearRepository.existsById(value)) {
      throw new YearNotFoundException(value);
    }
    yearRepository.deleteById(value);
  }

  @Override
  public void createYearIfNeeded(int value) {
    if (!yearRepository.existsById(value)) {
      Year year = Year.builder().value(value).build();
      yearRepository.saveAndFlush(year);
    }
  }
}
