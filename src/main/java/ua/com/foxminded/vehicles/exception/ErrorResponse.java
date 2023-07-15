package ua.com.foxminded.vehicles.exception;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    
    private String message;
    private String path;
    private List<ValidationError> validationErrors;
    
    public ErrorResponse() {
    }

    public ErrorResponse(String message, String path) {
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(List<ValidationError> validationErrors, String path) {
        this.validationErrors = validationErrors;
        this.path = path;
    }
}
