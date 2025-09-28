package com.redbus.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {
    private String code;
    private String message;
    private String details;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;
}
