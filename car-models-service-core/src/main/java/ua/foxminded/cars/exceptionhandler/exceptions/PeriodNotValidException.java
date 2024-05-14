package ua.foxminded.cars.exceptionhandler.exceptions;

import java.time.Year;

public class PeriodNotValidException extends RestrictionViolationException {

  public PeriodNotValidException(Year minYear, Year maxYear) {
    super(ExceptionMessages.PERIOD_NOT_VALID.formatted(minYear, maxYear));
  }
}
