package net.leozeballos.FastFood.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException ex, Model model, WebRequest request) {
        log.error("Entity not found: {}", ex.getMessage());
        populateErrorModel(model, HttpStatus.NOT_FOUND, "Entity Not Found", ex.getMessage(), request.getDescription(false));
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model, WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage());
        populateErrorModel(model, HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getDescription(false));
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationExceptions(MethodArgumentNotValidException ex, Model model, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Validation failed: {}", errors);
        
        populateErrorModel(model, HttpStatus.BAD_REQUEST, "Validation Error", "Invalid input data", request.getDescription(false));
        model.addAttribute("validationErrors", errors);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception ex, Model model, WebRequest request) {
        log.error("Internal Server Error: ", ex);
        populateErrorModel(model, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", 
            "The server encountered an unexpected error.", request.getDescription(false));
        return "error";
    }

    private void populateErrorModel(Model model, HttpStatus status, String error, String message, String path) {
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("error_status", status.value());
        model.addAttribute("error_message", error);
        model.addAttribute("error_description", message);
        model.addAttribute("path", path);
        model.addAttribute("pageTitle", "Error - " + error);
    }
}
