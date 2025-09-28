package com.redbus.journey.dto;

import com.redbus.journey.entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatConfigDto {
    private String seatNumber;
    private SeatType seatType;
    private Boolean isLadiesSeat = false;
    private Double fareMultiplier = 1.0;
}
