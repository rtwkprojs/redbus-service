package com.redbus.journey.controller;

import com.redbus.common.dto.ApiResponse;
import com.redbus.journey.dto.SeatInventoryDto;
import com.redbus.journey.service.SeatManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/journeys/{journeyReferenceId}/seats")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class SeatManagementController {
    
    private final SeatManagementService seatManagementService;
    
    @PostMapping("/lock")
    public ResponseEntity<ApiResponse<List<SeatInventoryDto>>> lockSeats(
            @PathVariable UUID journeyReferenceId,
            @RequestBody Map<String, List<UUID>> request) {
        log.info("Locking seats for journey: {}", journeyReferenceId);
        List<UUID> seatInventoryIds = request.get("seatInventoryIds");
        List<SeatInventoryDto> lockedSeats = seatManagementService.lockSeatsForBooking(journeyReferenceId, seatInventoryIds);
        return ResponseEntity.ok(ApiResponse.success(lockedSeats, "Seats locked successfully"));
    }
    
    @PostMapping("/update-booking")
    public ResponseEntity<ApiResponse<Void>> updateSeatBookingStatus(
            @PathVariable UUID journeyReferenceId,
            @RequestBody Map<String, Object> request) {
        log.info("Updating seat booking status for journey: {}", journeyReferenceId);
        @SuppressWarnings("unchecked")
        List<String> seatIds = (List<String>) request.get("seatInventoryIds");
        List<UUID> seatInventoryIds = seatIds.stream().map(UUID::fromString).collect(java.util.stream.Collectors.toList());
        String bookingReferenceId = (String) request.get("bookingReferenceId");
        
        seatManagementService.updateSeatBookingStatus(journeyReferenceId, seatInventoryIds, bookingReferenceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Seat booking status updated"));
    }
    
    @PostMapping("/release")
    public ResponseEntity<ApiResponse<Void>> releaseSeats(
            @PathVariable UUID journeyReferenceId,
            @RequestBody Map<String, List<UUID>> request) {
        log.info("Releasing seats for journey: {}", journeyReferenceId);
        List<UUID> seatInventoryIds = request.get("seatInventoryIds");
        seatManagementService.releaseSeats(journeyReferenceId, seatInventoryIds);
        return ResponseEntity.ok(ApiResponse.success(null, "Seats released successfully"));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SeatInventoryDto>>> getSeatInventory(
            @PathVariable UUID journeyReferenceId,
            @RequestParam(required = false) List<UUID> seatInventoryIds) {
        log.info("Getting seat inventory for journey: {}", journeyReferenceId);
        List<SeatInventoryDto> seats = seatManagementService.getSeatInventory(journeyReferenceId, seatInventoryIds);
        return ResponseEntity.ok(ApiResponse.success(seats));
    }
    
    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkSeatAvailability(
            @PathVariable UUID journeyReferenceId,
            @RequestParam List<UUID> seatInventoryIds) {
        log.info("Checking seat availability for journey: {}", journeyReferenceId);
        boolean available = seatManagementService.checkSeatAvailability(journeyReferenceId, seatInventoryIds);
        return ResponseEntity.ok(ApiResponse.success(available));
    }
}
