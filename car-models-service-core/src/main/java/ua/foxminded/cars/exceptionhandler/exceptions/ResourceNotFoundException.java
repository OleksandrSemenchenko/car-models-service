package ua.foxminded.cars.exceptionhandler.exceptions;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException() {
    super(ExceptionMessages.RESOURCE_NOT_FOUND);
  }
}
