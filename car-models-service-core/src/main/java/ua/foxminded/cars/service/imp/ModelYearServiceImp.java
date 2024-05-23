package ua.foxminded.cars.service.imp;

import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.foxminded.cars.exceptionhandler.exceptions.YearNotFoundException;
import ua.foxminded.cars.mapper.ModelYearMapper;
import ua.foxminded.cars.repository.ModelYearRepository;
import ua.foxminded.cars.repository.entity.ModelYear;
import ua.foxminded.cars.service.ModelYearService;
import ua.foxminded.cars.service.dto.ModelYearDto;

@Service
@RequiredArgsConstructor
public class ModelYearServiceImp implements ModelYearService {

  private final ModelYearRepository modelYearRepository;
  private final ModelYearMapper modelYearMapper;

  @Override
  public boolean isModelYearExist(int year) {
    return false;
  }

  @Override
  public void deleteYear(int value) {
    Year year = Year.of(value);
    if (!modelYearRepository.existsById(year)) {
      throw new YearNotFoundException(value);
    }
    modelYearRepository.deleteById(year);
  }

  @Override
  public ModelYearDto createModelYear(ModelYearDto modelYearDto) {
    ModelYear modelYear = modelYearMapper.toEntity(modelYearDto);
    modelYearRepository.saveAndFlush(modelYear);
    return modelYearMapper.toDto(modelYear);
  }
}
