package ua.foxminded.cars.exceptionhandler.exceptions;

import ua.foxminded.cars.exceptionhandler.ExceptionMessages;

public class PeriodNotValidException extends UnitAlreadyExistsException {

  public PeriodNotValidException(Integer minYear, Integer maxYear) {
    super(ExceptionMessages.PERIOD_NOT_VALID.formatted(minYear, maxYear));
  }
}
