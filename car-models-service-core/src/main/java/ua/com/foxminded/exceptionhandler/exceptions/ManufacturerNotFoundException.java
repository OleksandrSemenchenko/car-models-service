package ua.com.foxminded.exceptionhandler.exceptions;

public class ManufacturerNotFoundException extends NotFoundException {
  
  public static final String MESSAGE = "The manufacturer '%s' doesn't exist";

  public ManufacturerNotFoundException(String manufacturerName) {
    super(MESSAGE.formatted(manufacturerName));
  }
}
