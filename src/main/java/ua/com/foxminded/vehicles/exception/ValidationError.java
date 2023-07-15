package ua.com.foxminded.vehicles.exception;

import lombok.Data;

@Data
public class ValidationError {
    
    private final String fieldError;
    private final String message;
}
