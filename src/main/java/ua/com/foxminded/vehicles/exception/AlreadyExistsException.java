package ua.com.foxminded.vehicles.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public AlreadyExistsException() {
        super();
    }

    public AlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExistsException(String message) {
        super(message);
    }
}
