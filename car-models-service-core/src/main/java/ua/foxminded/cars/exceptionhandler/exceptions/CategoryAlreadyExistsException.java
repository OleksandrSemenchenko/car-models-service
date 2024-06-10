package ua.foxminded.cars.exceptionhandler.exceptions;

import static ua.foxminded.cars.exceptionhandler.ExceptionMessages.CATEGORY_ALREADY_EXISTS;

import java.util.Collection;

public class CategoryAlreadyExistsException extends UnitAlreadyExistsException {

  public CategoryAlreadyExistsException(String categoryName) {
    super(CATEGORY_ALREADY_EXISTS.formatted(categoryName));
  }

  public CategoryAlreadyExistsException(Collection<String> messages) {
    super(String.join("\n", messages));
  }
}
