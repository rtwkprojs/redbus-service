package com.redbus.journey.dto;

import com.redbus.journey.enums.JourneyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JourneyResponseDto {
    private UUID referenceId;
    private String journeyCode;
    private UUID routeReferenceId;
    private String routeName;
    private String sourceCity;
    private String destinationCity;
    private String vehicleReferenceId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private JourneyStatus journeyStatus;
    private Integer totalSeats;
    private Integer availableSeats;
    private Double baseFare;
    private Boolean isActive;
    private String amenities;
    private Integer estimatedDurationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional getters for compatibility
    public Boolean getIsActive() {
        return isActive;
    }
}
