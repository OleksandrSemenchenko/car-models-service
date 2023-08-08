package ua.com.foxminded.vehicles.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    
private static final long serialVersionUID = 1L;
    
    private HttpStatus httpStatus;
    
    public ServiceException() {
        super();
    }
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceException(String message, HttpStatus httpStatus) {
        this(message);
        this.httpStatus = httpStatus;
    }
}
