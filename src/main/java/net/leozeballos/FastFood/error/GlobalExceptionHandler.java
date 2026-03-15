package net.leozeballos.FastFood.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.error("Entity not found: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.NOT_FOUND, "Entity Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Validation failed: {}", errors);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid input data");
        problemDetail.setTitle("Validation Error");
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex, WebRequest request) {
        log.error("Internal Server Error: ", ex);
        return buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
            "The server encountered an unexpected error.", request);
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        return problemDetail;
    }
}
