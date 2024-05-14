package ua.foxminded.cars.exceptionhandler.exceptions;

public class RestrictionViolationException extends RuntimeException {

  public RestrictionViolationException(String message) {
    super(message);
  }
}
