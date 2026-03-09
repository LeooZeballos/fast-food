package net.leozeballos.FastFood.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private boolean isApiRequest(WebRequest request) {
        String path = request.getDescription(false);
        return path != null && path.contains("/api/v1");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, Model model, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return buildJsonResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
        }
        populateErrorModel(model, HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request.getDescription(false));
        return "error";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public Object handleEntityNotFoundException(EntityNotFoundException ex, Model model, WebRequest request) {
        log.error("Entity not found: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return buildJsonResponse(HttpStatus.NOT_FOUND, "Entity Not Found", ex.getMessage(), request);
        }
        populateErrorModel(model, HttpStatus.NOT_FOUND, "Entity Not Found", ex.getMessage(), request.getDescription(false));
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleIllegalArgumentException(IllegalArgumentException ex, Model model, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        if (isApiRequest(request)) {
            return buildJsonResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
        }
        populateErrorModel(model, HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getDescription(false));
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidationExceptions(MethodArgumentNotValidException ex, Model model, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Validation failed: {}", errors);

        if (isApiRequest(request)) {
            ErrorResponseDTO body = ErrorResponseDTO.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Validation Error")
                    .message("Invalid input data")
                    .path(request.getDescription(false).replace("uri=", ""))
                    .validationErrors(errors)
                    .build();
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        populateErrorModel(model, HttpStatus.BAD_REQUEST, "Validation Error", "Invalid input data", request.getDescription(false));
        model.addAttribute("validationErrors", errors);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGlobalException(Exception ex, Model model, WebRequest request) {
        log.error("Internal Server Error: ", ex);
        if (isApiRequest(request)) {
            return buildJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "The server encountered an unexpected error.", request);
        }
        populateErrorModel(model, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
            "The server encountered an unexpected error.", request.getDescription(false));
        return "error";
    }

    private ResponseEntity<ErrorResponseDTO> buildJsonResponse(HttpStatus status, String error, String message, WebRequest request) {
        ErrorResponseDTO body = ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return new ResponseEntity<>(body, status);
    }

    private void populateErrorModel(Model model, HttpStatus status, String error, String message, String path) {
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("error_status", status.value());
        model.addAttribute("error_message", error);
        model.addAttribute("error_description", message);
        model.addAttribute("path", path.replace("uri=", ""));
        model.addAttribute("pageTitle", "Error - " + error);
    }
}
