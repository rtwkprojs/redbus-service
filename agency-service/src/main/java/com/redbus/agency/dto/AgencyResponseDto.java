package com.redbus.agency.dto;

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
public class AgencyResponseDto {
    private UUID referenceId;
    private String agencyName;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private Boolean isActive;
    private String ownerReferenceId;
    private Integer vehicleCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
