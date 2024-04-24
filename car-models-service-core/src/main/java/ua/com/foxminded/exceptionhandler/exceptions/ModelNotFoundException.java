package ua.com.foxminded.exceptionhandler.exceptions;

public class ModelNotFoundException extends NotFoundException {

  public static final String MESSAGE = 
      "The model with manufacturer '%s', name '%s' and year '%s' doesn't exist";
  
  public ModelNotFoundException(String manufacturer, String name, int year) {
    super(MESSAGE.formatted(year, manufacturer, name));
  }
}
