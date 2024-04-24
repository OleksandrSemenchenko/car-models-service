package ua.com.foxminded.exceptionhandler.exceptions;

public class ModelAlreadyExistsException extends AlreadyExistsException {
  
  private static final String MESSAGE = "Such model with id='%s' already exists";
  
  public ModelAlreadyExistsException(String modelId) {
    super(MESSAGE.formatted(modelId));
  }
}
