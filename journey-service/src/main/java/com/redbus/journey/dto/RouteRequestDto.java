package com.redbus.journey.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDto {
    
    @NotBlank(message = "Route name is required")
    @Size(max = 200, message = "Route name must not exceed 200 characters")
    private String routeName;
    
    @NotBlank(message = "Source city is required")
    @Size(max = 100, message = "Source city must not exceed 100 characters")
    private String sourceCity;
    
    @NotBlank(message = "Destination city is required")
    @Size(max = 100, message = "Destination city must not exceed 100 characters")
    private String destinationCity;
    
    @NotNull(message = "Distance is required")
    @Min(value = 1, message = "Distance must be at least 1 km")
    @Max(value = 5000, message = "Distance cannot exceed 5000 km")
    private Integer distanceKm;
    
    @NotNull(message = "Estimated duration is required")
    @Min(value = 30, message = "Duration must be at least 30 minutes")
    @Max(value = 2880, message = "Duration cannot exceed 48 hours")
    private Integer estimatedDurationMinutes;
    
    @NotNull(message = "Base fare is required")
    @DecimalMin(value = "100.0", message = "Base fare must be at least 100")
    @DecimalMax(value = "50000.0", message = "Base fare cannot exceed 50000")
    private Double baseFare;
    
    @NotNull(message = "Agency reference ID is required")
    private String agencyReferenceId;
    
    private List<RouteStopRequestDto> stops;
}
