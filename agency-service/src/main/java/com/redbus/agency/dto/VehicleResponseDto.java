package com.redbus.agency.dto;

import com.redbus.agency.entity.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDto {
    private UUID referenceId;
    private String registrationNumber;
    private VehicleType vehicleType;
    private Integer totalSeats;
    private String manufacturer;
    private String model;
    private Integer yearOfManufacture;
    private Boolean isActive;
    private Boolean hasAC;
    private Boolean hasWifi;
    private Boolean hasChargingPoints;
    private UUID agencyReferenceId;
    private String agencyName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
