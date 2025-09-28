package com.redbus.agency.dto;

import com.redbus.agency.entity.VehicleType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDto {
    
    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,2}[0-9]{4}$", 
            message = "Registration number must be in format: XX00XX0000")
    private String registrationNumber;
    
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;
    
    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    @Max(value = 100, message = "Total seats cannot exceed 100")
    private Integer totalSeats;
    
    @Size(max = 50, message = "Manufacturer name must not exceed 50 characters")
    private String manufacturer;
    
    @Size(max = 50, message = "Model name must not exceed 50 characters")
    private String model;
    
    @Min(value = 1990, message = "Year of manufacture must be 1990 or later")
    @Max(value = 2030, message = "Year of manufacture cannot exceed 2030")
    private Integer yearOfManufacture;
    
    private Boolean hasAC = false;
    private Boolean hasWifi = false;
    private Boolean hasChargingPoints = false;
}
