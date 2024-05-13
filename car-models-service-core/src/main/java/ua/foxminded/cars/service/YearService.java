package ua.foxminded.cars.service;

public interface YearService {

  void deleteYear(int year);

  void createYearIfNeeded(int year);
}
