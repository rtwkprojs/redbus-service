package com.redbus.agency.controller;

import com.redbus.agency.dto.AgencyRequestDto;
import com.redbus.agency.dto.AgencyResponseDto;
import com.redbus.agency.dto.VehicleRequestDto;
import com.redbus.agency.dto.VehicleResponseDto;
import com.redbus.agency.service.AgencyService;
import com.redbus.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agencies")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class AgencyController {
    
    private final AgencyService agencyService;
    
    // For now, we'll hardcode the owner reference ID - in production, this would come from JWT
    private static final String OWNER_REFERENCE_ID = "test-owner-id";
    
    @PostMapping
    public ResponseEntity<ApiResponse<AgencyResponseDto>> createAgency(
            @Valid @RequestBody AgencyRequestDto requestDto) {
        log.info("Creating agency: {}", requestDto.getAgencyName());
        AgencyResponseDto agency = agencyService.createAgency(requestDto, OWNER_REFERENCE_ID);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(agency, "Agency created successfully"));
    }
    
    @PutMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<AgencyResponseDto>> updateAgency(
            @PathVariable UUID referenceId,
            @Valid @RequestBody AgencyRequestDto requestDto) {
        log.info("Updating agency: {}", referenceId);
        AgencyResponseDto agency = agencyService.updateAgency(referenceId, requestDto, OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(agency, "Agency updated successfully"));
    }
    
    @GetMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<AgencyResponseDto>> getAgency(@PathVariable UUID referenceId) {
        log.info("Fetching agency: {}", referenceId);
        AgencyResponseDto agency = agencyService.getAgencyByReferenceId(referenceId);
        return ResponseEntity.ok(ApiResponse.success(agency));
    }
    
    @GetMapping("/my-agencies")
    public ResponseEntity<ApiResponse<List<AgencyResponseDto>>> getMyAgencies() {
        log.info("Fetching agencies for owner: {}", OWNER_REFERENCE_ID);
        List<AgencyResponseDto> agencies = agencyService.getAgenciesByOwner(OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(agencies));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<AgencyResponseDto>>> getAllActiveAgencies() {
        log.info("Fetching all active agencies");
        List<AgencyResponseDto> agencies = agencyService.getAllActiveAgencies();
        return ResponseEntity.ok(ApiResponse.success(agencies));
    }
    
    @DeleteMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<Void>> deleteAgency(@PathVariable UUID referenceId) {
        log.info("Deleting agency: {}", referenceId);
        agencyService.deleteAgency(referenceId, OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(null, "Agency deleted successfully"));
    }
    
    @PostMapping("/{referenceId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateAgency(@PathVariable UUID referenceId) {
        log.info("Activating agency: {}", referenceId);
        agencyService.activateAgency(referenceId, OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(null, "Agency activated successfully"));
    }
    
    @PostMapping("/{referenceId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateAgency(@PathVariable UUID referenceId) {
        log.info("Deactivating agency: {}", referenceId);
        agencyService.deactivateAgency(referenceId, OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(null, "Agency deactivated successfully"));
    }
    
    // Vehicle endpoints
    @PostMapping("/{agencyReferenceId}/vehicles")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> addVehicle(
            @PathVariable UUID agencyReferenceId,
            @Valid @RequestBody VehicleRequestDto requestDto) {
        log.info("Adding vehicle to agency: {}", agencyReferenceId);
        VehicleResponseDto vehicle = agencyService.addVehicle(agencyReferenceId, requestDto, OWNER_REFERENCE_ID);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(vehicle, "Vehicle added successfully"));
    }
    
    @PutMapping("/{agencyReferenceId}/vehicles/{vehicleReferenceId}")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> updateVehicle(
            @PathVariable UUID agencyReferenceId,
            @PathVariable UUID vehicleReferenceId,
            @Valid @RequestBody VehicleRequestDto requestDto) {
        log.info("Updating vehicle: {} in agency: {}", vehicleReferenceId, agencyReferenceId);
        VehicleResponseDto vehicle = agencyService.updateVehicle(agencyReferenceId, vehicleReferenceId, requestDto, OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(vehicle, "Vehicle updated successfully"));
    }
    
    @GetMapping("/{agencyReferenceId}/vehicles")
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> getVehiclesByAgency(
            @PathVariable UUID agencyReferenceId) {
        log.info("Fetching vehicles for agency: {}", agencyReferenceId);
        List<VehicleResponseDto> vehicles = agencyService.getVehiclesByAgency(agencyReferenceId);
        return ResponseEntity.ok(ApiResponse.success(vehicles));
    }
    
    @GetMapping("/vehicles/{vehicleReferenceId}")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> getVehicle(@PathVariable UUID vehicleReferenceId) {
        log.info("Fetching vehicle: {}", vehicleReferenceId);
        VehicleResponseDto vehicle = agencyService.getVehicleByReferenceId(vehicleReferenceId);
        return ResponseEntity.ok(ApiResponse.success(vehicle));
    }
    
    @DeleteMapping("/{agencyReferenceId}/vehicles/{vehicleReferenceId}")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(
            @PathVariable UUID agencyReferenceId,
            @PathVariable UUID vehicleReferenceId) {
        log.info("Deleting vehicle: {} from agency: {}", vehicleReferenceId, agencyReferenceId);
        agencyService.deleteVehicle(agencyReferenceId, vehicleReferenceId, OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(null, "Vehicle deleted successfully"));
    }
    
    @PostMapping("/{agencyReferenceId}/vehicles/{vehicleReferenceId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateVehicle(
            @PathVariable UUID agencyReferenceId,
            @PathVariable UUID vehicleReferenceId) {
        log.info("Activating vehicle: {} in agency: {}", vehicleReferenceId, agencyReferenceId);
        agencyService.activateVehicle(agencyReferenceId, vehicleReferenceId, OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(null, "Vehicle activated successfully"));
    }
    
    @PostMapping("/{agencyReferenceId}/vehicles/{vehicleReferenceId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateVehicle(
            @PathVariable UUID agencyReferenceId,
            @PathVariable UUID vehicleReferenceId) {
        log.info("Deactivating vehicle: {} in agency: {}", vehicleReferenceId, agencyReferenceId);
        agencyService.deactivateVehicle(agencyReferenceId, vehicleReferenceId, OWNER_REFERENCE_ID);
        return ResponseEntity.ok(ApiResponse.success(null, "Vehicle deactivated successfully"));
    }
}
