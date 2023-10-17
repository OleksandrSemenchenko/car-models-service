package ua.com.foxminded.exception;

import lombok.Data;

@Data
public class Violation {
    
    private final String attribute;
    private final String message;
}
