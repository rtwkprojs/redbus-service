package com.redbus.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatSelectionDto {
    
    @NotNull(message = "Seat inventory reference ID is required")
    private UUID seatInventoryReferenceId;
    
    @NotBlank(message = "Seat number is required")
    private String seatNumber;
    
    @NotNull(message = "Passenger index is required")
    private Integer passengerIndex; // Index in the passengers list
}
