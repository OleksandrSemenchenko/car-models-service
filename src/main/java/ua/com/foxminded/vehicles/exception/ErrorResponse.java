package ua.com.foxminded.vehicles.exception;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    
    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<ValidationError> validationErrors;
    
    public ErrorResponse() {
    }

    public ErrorResponse(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
