package com.project.back_end.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationFailed {

    // 2. Handle validation exceptions globally
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        // Extract all field errors messages
        List<String> errorMessages = ex.getBindingResult()
                                       .getFieldErrors()
                                       .stream()
                                       .map(FieldError::getDefaultMessage)
                                       .collect(Collectors.toList());

        // Join messages into a single string separated by commas (or handle as you prefer)
        String combinedMessage = String.join(", ", errorMessages);

        // Return a map with the message key
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", combinedMessage));
    }
}
