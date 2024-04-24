package ua.com.foxminded.exceptionhandler.exceptions;

import ua.com.foxminded.exception.AlreadyExistsException;

public class CategoryAlreadyExistsException extends AlreadyExistsException {
  
  private static final String MESSAGE = "The category '%s' already exists";
  
  public CategoryAlreadyExistsException(String categoryName) {
    super(MESSAGE.formatted(categoryName));
  }  
}
