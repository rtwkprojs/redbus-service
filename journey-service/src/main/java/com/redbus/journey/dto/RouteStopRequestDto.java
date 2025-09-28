package com.redbus.journey.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopRequestDto {
    
    private UUID stopId; // Use existing stop if provided
    
    // For creating new stop if stopId is not provided
    private String stopName;
    private String city;
    private String state;
    private String address;
    private Double latitude;
    private Double longitude;
    private String landmark;
    
    @NotNull(message = "Stop sequence is required")
    @Min(value = 1, message = "Stop sequence must be at least 1")
    private Integer stopSequence;
    
    @NotNull(message = "Arrival time offset is required")
    @Min(value = 0, message = "Arrival time offset cannot be negative")
    private Integer arrivalTimeOffsetMinutes;
    
    @NotNull(message = "Departure time offset is required")
    @Min(value = 0, message = "Departure time offset cannot be negative")
    private Integer departureTimeOffsetMinutes;
    
    @Min(value = 0, message = "Distance cannot be negative")
    private Integer distanceFromPreviousKm;
    
    @DecimalMin(value = "0.0", message = "Fare cannot be negative")
    private Double fareFromOrigin;
    
    private Boolean isBoardingPoint = true;
    private Boolean isDroppingPoint = true;
}
