package ua.com.foxminded.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.com.foxminded.exception.ErrorResponse;
import ua.com.foxminded.exception.Violation;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerController {
    
    public static final String  DATA_INTEGRITY_VIOLATION_EXCEPTION_MESSAGE = 
            "The requested resource has relations to other resources";
    public static final String NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE = "The request has the not valid agrument(s)";
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(METHOD_NOT_ALLOWED)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("Data integrity violation exception", e);
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, DATA_INTEGRITY_VIOLATION_EXCEPTION_MESSAGE, request);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        log.error("Constraint violation exception", e);
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, 
                                                         NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE, 
                                                         request);
        List<Violation> violations = e.getConstraintViolations().stream()
                .map(violation -> new Violation(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        errorResponse.setViolations(violations);
        return errorResponse;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("Method argument not valid exception", e);
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, 
                                                         NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE, 
                                                         request);
        
        List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new Violation(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        errorResponse.setViolations(violations);
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
