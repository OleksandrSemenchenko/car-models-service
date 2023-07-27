package ua.com.foxminded.vehicles.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import ua.com.foxminded.vehicles.exception.ErrorResponse;
import ua.com.foxminded.vehicles.exception.ServiceException;
import ua.com.foxminded.vehicles.exception.ValidationError;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {
    
    @ExceptionHandler(ServletRequestBindingException.class)
    public ErrorResponse handleBindingException(ServletRequestBindingException e, 
                                                HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(BAD_REQUEST.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setTimestamp(new Date());
        return errorResponse;
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e, 
                                                                            HttpServletRequest request) {
        log.error("Constraint violation exception", e);
        List<ValidationError> violations = e.getConstraintViolations()
                .stream()
                .map(violation -> new ValidationError(violation.getPropertyPath().toString(), 
                                                      violation.getMessage()))
                .toList();
        ErrorResponse errorResponse = new ErrorResponse(violations);
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setStatus(BAD_REQUEST.value());
        errorResponse.setTimestamp(new Date());
        errorResponse.setError(BAD_REQUEST.getReasonPhrase());
        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleUncaughtExceptions(Exception e, HttpServletRequest request) {
        log.error("Uncought exception", e);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError(INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorResponse.setMessage(e.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setTimestamp(new Date());
        return errorResponse;
    }
    
    @ExceptionHandler(ServiceException.class)
    public ProblemDetail handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error("Service excetpion", e);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getHttpStatus(), e.getMessage());
        problemDetail.setTitle(e.getHttpStatus().getReasonPhrase());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", new Date());
        return problemDetail;
    }
    
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentViolation(MethodArgumentNotValidException e, 
                                                                       HttpServletRequest request) {
        log.error("Method argument validation exception");
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
