package ua.com.foxminded.exceptionhandler.exceptions;

public class YearNotFoundException extends NotFoundException {

  private static final String MESSAGE = "Year '%s' not found";

  public YearNotFoundException(int value) {
    super(MESSAGE.formatted(value));
  }
}
