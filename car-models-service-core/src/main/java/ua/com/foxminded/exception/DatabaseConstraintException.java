package ua.com.foxminded.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class DatabaseConstraintException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public DatabaseConstraintException(String message) {
        super(message);
    }
}
