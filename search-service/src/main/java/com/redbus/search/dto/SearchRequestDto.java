package com.redbus.search.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
    
    @NotBlank(message = "Source city is required")
    private String sourceCity;
    
    @NotBlank(message = "Destination city is required")
    private String destinationCity;
    
    @NotNull(message = "Travel date is required")
    @FutureOrPresent(message = "Travel date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate travelDate;
    
    @Min(value = 1, message = "At least 1 seat required")
    @Max(value = 10, message = "Maximum 10 seats allowed")
    private Integer seatsRequired = 1;
    
    // Filters
    private Double maxFare;
    private String vehicleType;
    private String agencyName;
    private Boolean acOnly;
    private Boolean sleeperOnly;
    
    // Sorting
    private String sortBy = "departureTime"; // departureTime, fare, duration
    private String sortOrder = "ASC"; // ASC, DESC
    
    // Pagination
    @Min(0)
    private Integer page = 0;
    
    @Min(1)
    @Max(50)
    private Integer size = 20;
}
