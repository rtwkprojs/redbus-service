package com.redbus.journey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDto {
    private UUID referenceId;
    private String routeName;
    private String sourceCity;
    private String destinationCity;
    private Integer distanceKm;
    private Integer estimatedDurationMinutes;
    private Double baseFare;
    private Boolean isActive;
    private String agencyReferenceId;
    private Integer stopCount;
    private List<RouteStopResponseDto> stops;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
