package ua.foxminded.cars.exceptionhandler.exceptions;

public class ManufacturerNotFoundException extends UnitNotFoundException {

  public ManufacturerNotFoundException(String manufacturerName) {
    super(ExceptionMessages.MANUFACTURER_NOT_FOUND.formatted(manufacturerName));
  }
}
