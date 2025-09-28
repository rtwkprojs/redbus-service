package com.redbus.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchFiltersDto {
    private String sourceCity;
    private String destinationCity;
    private LocalDate travelDate;
    private Integer seatsRequired;
    private Double maxFare;
    private String vehicleType;
    private String agencyName;
    private Boolean acOnly;
    private Boolean sleeperOnly;
}
