package ua.com.foxminded.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.exception.ErrorResponse;
import ua.com.foxminded.exception.Violation;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerController {
    
    public static final String  DATA_INTEGRRITY_VIOLATION_EXCEPTION_MESSAGE = 
            "The requested resource has relations to other resources";
    public static final String NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE = "The request has the not valid agrument(s)";
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ErrorResponse handleDataIntegrityViolation(HttpServletRequest request) {
        return buildErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED, DATA_INTEGRRITY_VIOLATION_EXCEPTION_MESSAGE, request);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        log.error("Constraint violation exception");
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST, NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE, request);
        
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errorResponse.getViolations().add((new Violation(violation.getPropertyPath().toString(), 
                                                             violation.getMessage())));
        }
        return errorResponse;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Method argument not valid exception");
        ErrorResponse errorResponse = buildErrorResponse(
                HttpStatus.BAD_REQUEST, NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE, request);
        
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorResponse.getViolations().add(new Violation(fieldError.getField(), 
                                                            fieldError.getDefaultMessage()));
        }
        return errorResponse;
    }
    
    private ErrorResponse buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        return ErrorResponse.builder().error(status.getReasonPhrase())
                                      .path(request.getRequestURI())
                                      .timestamp(Instant.now())
                                      .status(status.value())
                                      .message(message)
                                      .build();
    }
}
