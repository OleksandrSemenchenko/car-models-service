package ua.com.foxminded.exception;

import java.time.Instant;

import lombok.Data;

@Data
public class ErrorDetails {
    
    private Instant timestamp;
    private int status;
    private String error;
    private String exception;
    private String message;
    private String path;
}
