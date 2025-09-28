package com.redbus.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JourneySearchResultDto {
    private String journeyId;
    private String journeyCode;
    private String sourceCity;
    private String destinationCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer durationMinutes;
    private Integer distanceKm;
    private Double baseFare;
    private Integer availableSeats;
    private Integer totalSeats;
    private String agencyName;
    private String vehicleType;
    private String routeName;
    private List<String> amenities;
    private Boolean isActive;
    private String status;
}
