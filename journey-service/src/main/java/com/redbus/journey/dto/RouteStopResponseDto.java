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
public class RouteStopResponseDto {
    private UUID referenceId;
    private UUID stopReferenceId;
    private String stopName;
    private String city;
    private String state;
    private String address;
    private String landmark;
    private Integer stopSequence;
    private Integer arrivalTimeOffsetMinutes;
    private Integer departureTimeOffsetMinutes;
    private Integer distanceFromPreviousKm;
    private Double fareFromOrigin;
    private Boolean isBoardingPoint;
    private Boolean isDroppingPoint;
}
