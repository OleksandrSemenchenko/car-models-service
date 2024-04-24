package ua.com.foxminded.exceptionhandler.exceptions;

import ua.com.foxminded.exception.AlreadyExistsException;

public class ManufacturerAlreadyExistsException extends AlreadyExistsException {
  
  private static final String MESSAGE = "The manufacturer '%s' already exists";

  public ManufacturerAlreadyExistsException(String manufacturerName) {
    super(MESSAGE.formatted(manufacturerName));
  }
}
