package ua.foxminded.cars.exceptionhandler.exceptions;

public class ResourceNotFoundException extends RuntimeException {
  private static final String MESSAGE = "Resource by the provided path not found";

  public ResourceNotFoundException() {
    super(MESSAGE);
  }
}
