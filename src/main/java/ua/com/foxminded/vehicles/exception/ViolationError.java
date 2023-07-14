package ua.com.foxminded.vehicles.exception;

import lombok.Data;

@Data
public class ViolationError {
    
    private final String fieldError;
    private final String message;
}
