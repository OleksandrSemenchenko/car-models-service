package ua.foxminded.cars.exceptionhandler.exceptions;

import ua.foxminded.cars.exceptionhandler.ExceptionMessages;

public class YearNotFoundException extends UnitNotFoundException {

  private static final String MESSAGE = "Year '%s' not found";

  public YearNotFoundException(int value) {
    super(ExceptionMessages.YEAR_NOT_FOUND.formatted(value));
  }
}
