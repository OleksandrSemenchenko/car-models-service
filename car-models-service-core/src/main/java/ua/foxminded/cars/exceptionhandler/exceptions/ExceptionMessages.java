package ua.foxminded.cars.exceptionhandler.exceptions;

public class ExceptionMessages {

  public static final String YEAR_NOT_FOUND = "Year '%s' not found";
  public static final String MODEL_NOT_FOUND_BY_ID = "The model with id=%s not found";
  public static final String MODEL_NOT_FOUND =
      "The model with manufacturer '%s', name '%s' and year '%s' not found";
  public static final String RESOURCE_NOT_FOUND = "Resource by the provided path not found";
  public static final String MODEL_ALREADY_EXIST_BY_PARAMETERS =
      "The model with manufacturer '%s', name '%s' and year '%s' already exists, its id='%s'";
  public static final String MANUFACTURER_NOT_FOUND = "The %s manufacturer not found";
  public static final String CATEGORY_NOT_FOUND = "The %s category not found";
  public static final String PERIOD_NOT_VALID = "The minYear=%s must be before maxYear=%s";

  private ExceptionMessages() {}
}
