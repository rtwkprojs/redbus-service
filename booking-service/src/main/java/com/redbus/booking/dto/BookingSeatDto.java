package com.redbus.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeatDto {
    private UUID referenceId;
    private UUID seatInventoryReferenceId;
    private String seatNumber;
    private UUID passengerReferenceId;
    private Double seatFare;
    private Boolean isLocked;
}
