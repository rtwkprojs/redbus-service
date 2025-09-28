package com.redbus.journey.dto;

import com.redbus.journey.entity.SeatType;
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
    private SeatType seatType;
    private Boolean isAvailable;
    private Boolean isLadiesSeat;
    private Double fareMultiplier;
    private Double calculatedFare;
    private String bookingReferenceId;
}
