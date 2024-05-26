package ua.foxminded.cars.exceptionhandler.exceptions;

import ua.foxminded.cars.exceptionhandler.ExceptionMessages;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException() {
    super(ExceptionMessages.RESOURCE_NOT_FOUND);
  }
}
