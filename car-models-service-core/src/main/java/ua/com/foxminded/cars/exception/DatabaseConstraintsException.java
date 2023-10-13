package ua.com.foxminded.cars.exception;

public class DatabaseConstraintsException extends ServiceException {

    private static final long serialVersionUID = 1L;

    public DatabaseConstraintsException(String message) {
        super(message);
    }
}
