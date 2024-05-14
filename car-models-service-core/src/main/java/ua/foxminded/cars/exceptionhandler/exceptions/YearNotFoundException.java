package ua.foxminded.cars.exceptionhandler.exceptions;

import java.time.Year;

public class YearNotFoundException extends UnitNotFoundException {

  private static final String MESSAGE = "Year '%s' not found";

  public YearNotFoundException(Year value) {
    super(ExceptionMessages.YEAR_NOT_FOUND.formatted(value));
  }
}
