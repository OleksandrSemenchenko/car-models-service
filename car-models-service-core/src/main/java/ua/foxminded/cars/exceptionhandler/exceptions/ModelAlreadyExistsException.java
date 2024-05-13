package ua.foxminded.cars.exceptionhandler.exceptions;

import java.util.UUID;

public class ModelAlreadyExistsException extends DataIntegrityViolationException {

  public ModelAlreadyExistsException(String manufacturer, String name, int year, UUID modelId) {
    super(
        ExceptionMessages.MODEL_ALREADY_EXIST_BY_PARAMETERS.formatted(
            manufacturer, name, year, String.valueOf(modelId)));
  }
}
