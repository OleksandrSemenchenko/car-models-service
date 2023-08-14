package ua.com.foxminded.vehicles.exception;

import lombok.Data;

@Data
public class Violation {
    
    private final String attribute;
    private final String message;
}
