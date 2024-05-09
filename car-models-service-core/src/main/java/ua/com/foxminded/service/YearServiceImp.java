package ua.com.foxminded.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.foxminded.exceptionhandler.exceptions.YearNotFoundException;
import ua.com.foxminded.repository.YearRepository;
import ua.com.foxminded.repository.entity.Year;

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
      yearRepository.save(year);
    }
  }
}
