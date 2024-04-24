package ua.com.foxminded.exceptionhandler.exceptions;

public class ModelNameNotFoundException extends NotFoundException {
  
  private static final String MESSAGE = "The model name '%s' doesn't exist";

  public ModelNameNotFoundException(String modelName) {
    super(MESSAGE.formatted(modelName));
  }
}
