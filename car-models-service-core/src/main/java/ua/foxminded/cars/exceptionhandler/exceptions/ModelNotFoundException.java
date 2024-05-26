package ua.foxminded.cars.exceptionhandler.exceptions;

import java.util.UUID;
import ua.foxminded.cars.exceptionhandler.ExceptionMessages;

public class ModelNotFoundException extends UnitNotFoundException {

  public ModelNotFoundException(UUID modelId) {
    super(ExceptionMessages.MODEL_NOT_FOUND_BY_ID.formatted(modelId.toString()));
  }

  public ModelNotFoundException(String manufacturer, String name, int year) {
    super(ExceptionMessages.MODEL_NOT_FOUND.formatted(manufacturer, name, year));
  }
}
