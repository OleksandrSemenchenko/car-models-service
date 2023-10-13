package ua.com.foxminded.cars.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class DatabaseConstraintsException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public DatabaseConstraintsException(String message) {
        super(message);
    }
}
