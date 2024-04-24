package ua.com.foxminded.exceptionhandler.exceptions;

public class CategoryNotFoundException extends NotFoundException {
  
  private static final String MESSAGE = "The category '%s' doesn't exist";
  
  public CategoryNotFoundException(String categoryName) {
    super(MESSAGE.formatted(categoryName));
  }
}
