package ua.foxminded.cars.exceptionhandler.exceptions;

import ua.foxminded.cars.exceptionhandler.ExceptionMessages;

public class ManufacturerNotFoundException extends UnitNotFoundException {

  public ManufacturerNotFoundException(String manufacturerName) {
    super(ExceptionMessages.MANUFACTURER_NOT_FOUND.formatted(manufacturerName));
  }
}
