package com.redbus.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ValidationException extends BusinessException {
    
    private final Map<String, String> errors;
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.errors = null;
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.errors = errors;
    }
}
