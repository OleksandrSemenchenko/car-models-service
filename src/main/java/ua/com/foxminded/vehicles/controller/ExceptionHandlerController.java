package ua.com.foxminded.vehicles.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.Instant;
import java.util.List;

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
import ua.com.foxminded.vehicles.exception.CategoryNotFoundException;
import ua.com.foxminded.vehicles.exception.ErrorResponse;
import ua.com.foxminded.vehicles.exception.ManufacturerNotFoundException;
import ua.com.foxminded.vehicles.exception.ModelNotFoundException;
import ua.com.foxminded.vehicles.exception.ValidationError;
import ua.com.foxminded.vehicles.exception.VehicleNotFoundException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {
    
    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleBindingException(ServletRequestBindingException e, 
                                                HttpServletRequest request) {
        log.error("Binding exception", e);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Exception e, HttpServletRequest request) {
        log.error("Unexpected exception", e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
    }
    
    @ExceptionHandler({CategoryNotFoundException.class, ManufacturerNotFoundException.class, 
                       ModelNotFoundException.class, VehicleNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(RuntimeException e, 
                                                                      HttpServletRequest request) {
        log.error("Not such element excetpion", e);
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
        return ResponseEntity.status(NOT_FOUND)
                             .body(errorResponse);
    }
    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentViolation(MethodArgumentNotValidException e, 
                                                       HttpServletRequest request) {
        log.error("Method argument not valid exception");
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
        List<ValidationError> validationErrors = buildValidationError(e);
        errorResponse.setValidationErrors(validationErrors);
        return errorResponse;
    }
    
    private ErrorResponse buildErrorResponse(HttpStatus status, Exception e, HttpServletRequest request) {
        return ErrorResponse.builder().error(status.getReasonPhrase())
                                      .message(e.getMessage())
                                      .path(request.getRequestURI())
                                      .timestamp(Instant.now())
                                      .status(status.value()).build();
    }
    
    private List<ValidationError> buildValidationError(BindException exception) {
        return exception.getBindingResult()
                        .getFieldErrors()
                        .stream().map(fieldError -> new ValidationError(fieldError.getField(), 
                                                                        fieldError.getDefaultMessage()))
                        .toList();
    }
}
