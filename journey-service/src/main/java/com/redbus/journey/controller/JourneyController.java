package com.redbus.journey.controller;

import com.redbus.common.dto.ApiResponse;
import com.redbus.journey.dto.JourneyRequestDto;
import com.redbus.journey.dto.JourneyResponseDto;
import com.redbus.journey.enums.JourneyStatus;
import com.redbus.journey.service.JourneyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class JourneyController {
    
    private final JourneyService journeyService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<JourneyResponseDto>> createJourney(
            @Valid @RequestBody JourneyRequestDto requestDto) {
        log.info("Creating journey for route: {}", requestDto.getRouteReferenceId());
        JourneyResponseDto journey = journeyService.createJourney(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(journey, "Journey created successfully"));
    }
    
    @PutMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<JourneyResponseDto>> updateJourney(
            @PathVariable UUID referenceId,
            @Valid @RequestBody JourneyRequestDto requestDto) {
        log.info("Updating journey: {}", referenceId);
        JourneyResponseDto journey = journeyService.updateJourney(referenceId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(journey, "Journey updated successfully"));
    }
    
    @GetMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<JourneyResponseDto>> getJourney(@PathVariable UUID referenceId) {
        log.info("Fetching journey: {}", referenceId);
        JourneyResponseDto journey = journeyService.getJourneyById(referenceId);
        return ResponseEntity.ok(ApiResponse.success(journey));
    }
    
    @GetMapping("/code/{journeyCode}")
    public ResponseEntity<ApiResponse<JourneyResponseDto>> getJourneyByCode(@PathVariable String journeyCode) {
        log.info("Fetching journey by code: {}", journeyCode);
        JourneyResponseDto journey = journeyService.getJourneyByCode(journeyCode);
        return ResponseEntity.ok(ApiResponse.success(journey));
    }
    
    @GetMapping("/route/{routeReferenceId}")
    public ResponseEntity<ApiResponse<List<JourneyResponseDto>>> getJourneysByRoute(
            @PathVariable UUID routeReferenceId) {
        log.info("Fetching journeys for route: {}", routeReferenceId);
        List<JourneyResponseDto> journeys = journeyService.getJourneysByRoute(routeReferenceId);
        return ResponseEntity.ok(ApiResponse.success(journeys));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<JourneyResponseDto>>> searchJourneys(
            @RequestParam String sourceCity,
            @RequestParam String destinationCity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate travelDate) {
        log.info("Searching journeys from {} to {} on {}", sourceCity, destinationCity, travelDate);
        List<JourneyResponseDto> journeys = journeyService.searchJourneys(sourceCity, destinationCity, travelDate);
        return ResponseEntity.ok(ApiResponse.success(journeys));
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<JourneyResponseDto>>> getUpcomingJourneys() {
        log.info("Fetching upcoming journeys");
        List<JourneyResponseDto> journeys = journeyService.getUpcomingJourneys();
        return ResponseEntity.ok(ApiResponse.success(journeys));
    }
    
    // Seat inventory endpoint moved to SeatManagementController to avoid ambiguity
    // Use GET /api/v1/journeys/{journeyReferenceId}/seats instead
    
    @PostMapping("/{referenceId}/status")
    public ResponseEntity<ApiResponse<Void>> updateJourneyStatus(
            @PathVariable UUID referenceId,
            @RequestParam JourneyStatus status) {
        log.info("Updating journey {} status to {}", referenceId, status);
        journeyService.updateJourneyStatus(referenceId, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Journey status updated successfully"));
    }
    
    @PostMapping("/{referenceId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelJourney(@PathVariable UUID referenceId) {
        log.info("Cancelling journey: {}", referenceId);
        journeyService.cancelJourney(referenceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Journey cancelled successfully"));
    }
    
    @PostMapping("/{referenceId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateJourney(@PathVariable UUID referenceId) {
        log.info("Activating journey: {}", referenceId);
        journeyService.activateJourney(referenceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Journey activated successfully"));
    }
    
    @PostMapping("/{referenceId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateJourney(@PathVariable UUID referenceId) {
        log.info("Deactivating journey: {}", referenceId);
        journeyService.deactivateJourney(referenceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Journey deactivated successfully"));
    }
    
    @GetMapping("/vehicle/{vehicleReferenceId}/schedule")
    public ResponseEntity<ApiResponse<List<JourneyResponseDto>>> getVehicleSchedule(
            @PathVariable String vehicleReferenceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Fetching vehicle {} schedule for {}", vehicleReferenceId, date);
        List<JourneyResponseDto> journeys = journeyService.getVehicleSchedule(vehicleReferenceId, date);
        return ResponseEntity.ok(ApiResponse.success(journeys));
    }
}
