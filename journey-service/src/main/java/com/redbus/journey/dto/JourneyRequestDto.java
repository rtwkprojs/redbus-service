package com.redbus.journey.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JourneyRequestDto {
    
    @NotNull(message = "Route reference ID is required")
    private UUID routeReferenceId;
    
    @NotBlank(message = "Vehicle reference ID is required")
    private String vehicleReferenceId;
    
    @NotNull(message = "Departure time is required")
    @Future(message = "Departure time must be in the future")
    private LocalDateTime departureTime;
    
    @DecimalMin(value = "100.0", message = "Base fare must be at least 100")
    @DecimalMax(value = "50000.0", message = "Base fare cannot exceed 50000")
    private Double baseFare; // Optional, will use route's base fare if not provided
    
    private String amenities; // JSON string of amenities
    
    private List<SeatConfigDto> seatConfiguration; // Optional custom seat configuration
}
