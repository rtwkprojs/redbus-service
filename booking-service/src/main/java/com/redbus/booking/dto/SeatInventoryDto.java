package com.redbus.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatInventoryDto {
    private UUID referenceId;
    private String seatNumber;
    private String seatType;
    private Boolean isAvailable;
    private Boolean isLadiesSeat;
    private Double fareMultiplier;
    private Double calculatedFare;
    private String bookingReferenceId;
}
