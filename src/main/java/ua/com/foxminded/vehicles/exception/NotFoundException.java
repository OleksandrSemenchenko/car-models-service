package ua.com.foxminded.vehicles.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ServiceException {

    private static final long serialVersionUID = 1L;
    
    public NotFoundException() {
        super();
    }

    public NotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
