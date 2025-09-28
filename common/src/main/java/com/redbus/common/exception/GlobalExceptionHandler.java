package com.redbus.common.exception;

import com.redbus.common.dto.ApiResponse;
import com.redbus.common.dto.ErrorDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        log.error("Business exception occurred: {}", ex.getMessage());
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, ex.getStatus());
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        
        log.error("Validation exception: {}", ex.getMessage());
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code("VALIDATION_ERROR")
                .message(ex.getMessage())
                .validationErrors(ex.getErrors())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error(ex.getMessage(), errorDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code("VALIDATION_ERROR")
                .message("Validation failed")
                .validationErrors(errors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error("Validation failed", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected error occurred", ex);
        
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code("INTERNAL_ERROR")
                .message("An unexpected error occurred")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        
        ApiResponse<Void> response = ApiResponse.error("Internal server error", errorDetails);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
