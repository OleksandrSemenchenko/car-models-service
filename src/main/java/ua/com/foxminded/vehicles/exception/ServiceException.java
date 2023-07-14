package ua.com.foxminded.vehicles.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private ErrorCode errorCode;
    
    public ServiceException() {
        super();
    }
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(ErrorCode errorCode) {
        this(errorCode.getDescription());
        this.errorCode = errorCode;
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ServiceException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ServiceException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, cause.getMessage(), cause);
    }
}
