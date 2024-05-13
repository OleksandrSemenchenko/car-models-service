package ua.foxminded.cars.exceptionhandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ua.foxminded.cars.exceptionhandler.exceptions.DataIntegrityViolationException;
import ua.foxminded.cars.exceptionhandler.exceptions.UnitNotFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ServiceExceptionHandler {

  private static final String DETAILS_FIELD = "details";
  private static final String ERROR_CODE_FIELD = "errorCode";
  private static final String TIMESTAMP_FILED = "timestamp";

  @ExceptionHandler(ConstraintViolationException.class)
  protected ResponseEntity<Object> handleConstraintViolationException(
      ConstraintViolationException e) {
    Map<String, String> violationDetails = getConstraintViolationDetails(e);
    Map<String, Object> responseBody = buildResponseBody(HttpStatus.BAD_REQUEST, violationDetails);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
  }

  private Map<String, String> getConstraintViolationDetails(ConstraintViolationException e) {
    Map<String, String> violationDetails = new HashMap<>();

    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      violationDetails.put(String.valueOf(violation.getPropertyPath()), violation.getMessage());
    }
    return violationDetails;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  protected ResponseEntity<Object> handleDataIntegrityConstraintViolationException(
      DataIntegrityViolationException e) {
    Map<String, Object> responseBody = buildResponseBody(HttpStatus.CONFLICT, e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
  }

  @ExceptionHandler(UnitNotFoundException.class)
  protected ResponseEntity<Object> handleUnitNotFoundException(UnitNotFoundException e) {
    Map<String, Object> responseBody = buildResponseBody(HttpStatus.NOT_FOUND, e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
  }

  private Map<String, Object> buildResponseBody(HttpStatus status, Object message) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put(TIMESTAMP_FILED, LocalDateTime.now());
    body.put(ERROR_CODE_FIELD, status.value());
    body.put(DETAILS_FIELD, message);
    return body;
  }

  //  public static final String DATA_INTEGRITY_VIOLATION_EXCEPTION_MESSAGE =
  //      "The requested resource has relations to other resources";
  //  public static final String NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE =
  //      "The request has the not valid argument(s)";

  //  @ExceptionHandler(DataIntegrityViolationException.class)
  //  @ResponseStatus(METHOD_NOT_ALLOWED)
  //  public ErrorResponse handleDataIntegrityViolation(
  //      DataIntegrityViolationException e, HttpServletRequest request) {
  //    return buildErrorResponse(
  //        HttpStatus.METHOD_NOT_ALLOWED, DATA_INTEGRITY_VIOLATION_EXCEPTION_MESSAGE, request);
  //  }
  //
  //  @ExceptionHandler(ConstraintViolationException.class)
  //  @ResponseStatus(BAD_REQUEST)
  //  public ErrorResponse handleConstraintViolation(
  //      ConstraintViolationException e, HttpServletRequest request) {
  //    ErrorResponse errorResponse =
  //        buildErrorResponse(HttpStatus.BAD_REQUEST, NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE,
  // request);
  //    List<Violation> violations =
  //        e.getConstraintViolations().stream()
  //            .map(
  //                violation ->
  //                    new Violation(violation.getPropertyPath().toString(),
  // violation.getMessage()))
  //            .toList();
  //    errorResponse.setViolations(violations);
  //    return errorResponse;
  //  }
  //
  //  @ExceptionHandler(MethodArgumentNotValidException.class)
  //  @ResponseStatus(BAD_REQUEST)
  //  public ErrorResponse handleMethodArgumentNotValid(
  //      MethodArgumentNotValidException e, HttpServletRequest request) {
  //    ErrorResponse errorResponse =
  //        buildErrorResponse(HttpStatus.BAD_REQUEST, NOT_VALID_ARGUMENT_EXCEPTION_MESSAGE,
  // request);
  //
  //    List<Violation> violations =
  //        e.getBindingResult().getFieldErrors().stream()
  //            .map(fieldError -> new Violation(fieldError.getField(),
  // fieldError.getDefaultMessage()))
  //            .toList();
  //    errorResponse.setViolations(violations);
  //    return errorResponse;
  //  }
  //
  //  private ErrorResponse buildErrorResponse(
  //      HttpStatus status, String message, HttpServletRequest request) {
  //    return ErrorResponse.builder()
  //        .error(status.getReasonPhrase())
  //        .path(request.getRequestURI())
  //        .timestamp(Instant.now())
  //        .status(status.value())
  //        .message(message)
  //        .build();
  //  }
}
