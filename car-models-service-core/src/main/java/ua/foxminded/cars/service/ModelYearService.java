package ua.foxminded.cars.service;

import ua.foxminded.cars.service.dto.ModelYearDto;

public interface ModelYearService {

  void deleteYear(int year);

  ModelYearDto createModelYear(ModelYearDto year);

  boolean isModelYearExist(int year);
}
