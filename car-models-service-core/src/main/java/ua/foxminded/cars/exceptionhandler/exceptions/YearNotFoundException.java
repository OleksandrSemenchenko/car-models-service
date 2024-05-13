package ua.foxminded.cars.exceptionhandler.exceptions;

public class YearNotFoundException extends UnitNotFoundException {

  private static final String MESSAGE = "Year '%s' not found";

  public YearNotFoundException(int value) {
    super(ExceptionMessages.YEAR_NOT_FOUND.formatted(value));
  }
}
