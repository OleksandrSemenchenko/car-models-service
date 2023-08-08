package ua.com.foxminded.vehicles.exception;

public class ManufacturerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ManufacturerNotFoundException(String message) {
        super(message);
    }
}
