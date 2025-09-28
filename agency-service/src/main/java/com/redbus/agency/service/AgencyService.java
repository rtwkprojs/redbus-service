package com.redbus.agency.service;

import com.redbus.agency.dto.AgencyRequestDto;
import com.redbus.agency.dto.AgencyResponseDto;
import com.redbus.agency.dto.VehicleRequestDto;
import com.redbus.agency.dto.VehicleResponseDto;

import java.util.List;
import java.util.UUID;

public interface AgencyService {
    
    AgencyResponseDto createAgency(AgencyRequestDto requestDto, String ownerReferenceId);
    
    AgencyResponseDto updateAgency(UUID referenceId, AgencyRequestDto requestDto, String ownerReferenceId);
    
    AgencyResponseDto getAgencyByReferenceId(UUID referenceId);
    
    List<AgencyResponseDto> getAgenciesByOwner(String ownerReferenceId);
    
    List<AgencyResponseDto> getAllActiveAgencies();
    
    void deleteAgency(UUID referenceId, String ownerReferenceId);
    
    void activateAgency(UUID referenceId, String ownerReferenceId);
    
    void deactivateAgency(UUID referenceId, String ownerReferenceId);
    
    VehicleResponseDto addVehicle(UUID agencyReferenceId, VehicleRequestDto requestDto, String ownerReferenceId);
    
    VehicleResponseDto updateVehicle(UUID agencyReferenceId, UUID vehicleReferenceId, VehicleRequestDto requestDto, String ownerReferenceId);
    
    List<VehicleResponseDto> getVehiclesByAgency(UUID agencyReferenceId);
    
    VehicleResponseDto getVehicleByReferenceId(UUID vehicleReferenceId);
    
    void deleteVehicle(UUID agencyReferenceId, UUID vehicleReferenceId, String ownerReferenceId);
    
    void activateVehicle(UUID agencyReferenceId, UUID vehicleReferenceId, String ownerReferenceId);
    
    void deactivateVehicle(UUID agencyReferenceId, UUID vehicleReferenceId, String ownerReferenceId);
}
