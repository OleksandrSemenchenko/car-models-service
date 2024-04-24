package ua.com.foxminded.exceptionhandler.exceptions;

public class ModelNameAlreadyExistsException extends AlreadyExistsException {
  
  private static final String MESSAGE = "The model name '%s' already exists";

  public ModelNameAlreadyExistsException(String modelName) {
    super(MESSAGE.formatted(modelName));
  }
}
