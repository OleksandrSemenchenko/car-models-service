package ua.com.foxminded.vehicles.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.vehicles.exception.ErrorResponse;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.exception.ValidationError;

@Slf4j
public class DefaultController {
    
    public static final int PAGE_NUMBER_DEF = 0;
    public static final int PAGE_SIZE_DEF = 100;
    
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ErrorResponse> handleBindingException(ServletRequestBindingException e, 
                                                                HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e, 
                                                                            HttpServletRequest request) {
        log.error("Violation error", e);
        List<ValidationError> violations = e.getConstraintViolations().stream()
                .map(violation -> new ValidationError(violation.getPropertyPath().toString(), 
                                             violation.getMessage()))
                .toList();
        ErrorResponse errorResponse = new ErrorResponse(violations, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception e, HttpServletRequest request) {
        log.error("Uncaught excetion", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error("Service excetpion", e);
        ErrorResponse errorRespose = new ErrorResponse(e.getErrorCode().getDescription(), 
                                                       request.getRequestURI());
        
        return ResponseEntity.status(e.getErrorCode().getCode()).body(errorRespose);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentViolation(MethodArgumentNotValidException e, 
                                                                       HttpServletRequest request) {
        log.error("Argument violation exception");
        List<ValidationError> validationErrors = buildValidationError(e);
        ErrorResponse errorResponse = new ErrorResponse(validationErrors, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errorResponse);
    }
    
    private List<ValidationError> buildValidationError(BindException exception) {
        return exception.getBindingResult()
                        .getFieldErrors()
                        .stream().map(fieldError -> new ValidationError(fieldError.getField(), 
                                                                       fieldError.getDefaultMessage()))
                        .toList();
    }
}
