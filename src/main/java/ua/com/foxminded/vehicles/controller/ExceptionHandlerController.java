package ua.com.foxminded.vehicles.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.vehicles.exception.ValidationErrorResponse;
import ua.com.foxminded.vehicles.exception.Violation;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ValidationErrorResponse handleConstraintViolation(ConstraintViolationException e, 
                                                             HttpServletRequest request) {
        log.error("Constraint violation exception");
        ValidationErrorResponse errorResponse = buildValidationErrorResponse(HttpStatus.BAD_REQUEST, request);
        
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errorResponse.getViolations().add((new Violation(violation.getPropertyPath().toString(), 
                                                             violation.getMessage())));
        }
        return errorResponse;
    }
    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e, 
                                                      HttpServletRequest request) {
        log.error("Method argument not valid exception");
        ValidationErrorResponse errorResponse = buildValidationErrorResponse(HttpStatus.BAD_REQUEST, request);
        
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorResponse.getViolations().add(new Violation(fieldError.getField(), 
                                                            fieldError.getDefaultMessage()));
        }
        return errorResponse;
    }
    
    private ValidationErrorResponse buildValidationErrorResponse(HttpStatus status, HttpServletRequest request) {
        return ValidationErrorResponse.builder().error(status.getReasonPhrase())
                                      .path(request.getRequestURI())
                                      .timestamp(Instant.now())
                                      .status(status.value()).build();
    }
}
