package ua.com.foxminded.exceptionhandler.exceptions;

public class CategoryAlreadyExistsException extends AlreadyExistsException {
  
  private static final String MESSAGE = "The category '%s' already exists";
  
  public CategoryAlreadyExistsException(String categoryName) {
    super(MESSAGE.formatted(categoryName));
  }  
}
