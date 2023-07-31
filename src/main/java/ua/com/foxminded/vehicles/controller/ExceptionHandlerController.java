package ua.com.foxminded.vehicles.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.vehicles.exception.ErrorResponse;
import ua.com.foxminded.vehicles.exception.ValidationError;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {
    
    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBindingException(ServletRequestBindingException e, 
                                                HttpServletRequest request) {
        log.error("Binding exception", e);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setTimestamp(new Date());
        return errorResponse;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("Unexpected exception", e);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(INTERNAL_SERVER_ERROR.value());
        return errorResponse;
    }
    
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e, 
                                                                      HttpServletRequest request) {
        log.error("Not such element excetpion", e);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(NOT_FOUND.value());
        return ResponseEntity.status(NOT_FOUND)
                             .body(errorResponse);
    }
    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentViolation(MethodArgumentNotValidException e, 
                                                       HttpServletRequest request) {
        log.error("Method argument not valid exception");
        List<ValidationError> validationErrors = buildValidationError(e);
        ErrorResponse errorResponse = new ErrorResponse(validationErrors);
        errorResponse.setError(BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setTimestamp(new Date());
        return errorResponse;
    }
    
    private List<ValidationError> buildValidationError(BindException exception) {
        return exception.getBindingResult()
                        .getFieldErrors()
                        .stream().map(fieldError -> new ValidationError(fieldError.getField(), 
                                                                        fieldError.getDefaultMessage()))
                        .toList();
    }
}
