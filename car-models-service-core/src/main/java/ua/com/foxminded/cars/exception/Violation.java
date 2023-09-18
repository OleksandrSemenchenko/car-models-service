package ua.com.foxminded.cars.exception;

import lombok.Data;

@Data
public class Violation {
    
    private final String attribute;
    private final String message;
}
