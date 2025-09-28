package com.redbus.journey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatInventoryDto {
    private UUID referenceId;
    private String seatNumber;
    private String seatType; // Changed to String for compatibility
    private Boolean isAvailable;
    private Boolean isLadiesSeat;
    private Double fareMultiplier;
    private Double calculatedFare;
    private String bookingReferenceId;
    
    // Additional getters for compatibility
    public Boolean getIsAvailable() {
        return isAvailable;
    }
}
