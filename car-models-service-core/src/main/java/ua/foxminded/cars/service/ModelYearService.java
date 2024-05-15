package ua.foxminded.cars.service;

import java.time.Year;

public interface ModelYearService {

  void deleteYear(Year year);

  void createYearIfNecessary(Year year);
}
