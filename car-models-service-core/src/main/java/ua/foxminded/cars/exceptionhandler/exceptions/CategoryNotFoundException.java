package ua.foxminded.cars.exceptionhandler.exceptions;

import java.util.List;
import ua.foxminded.cars.exceptionhandler.ExceptionMessages;

public class CategoryNotFoundException extends UnitNotFoundException {

  public CategoryNotFoundException(String categoryName) {
    super(ExceptionMessages.CATEGORY_NOT_FOUND.formatted(categoryName));
  }

  public CategoryNotFoundException(List<String> messages) {
    super(String.join("\n", messages));
  }
}
