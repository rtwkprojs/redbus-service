package com.redbus.booking.controller;

import com.redbus.booking.dto.BookingRequestDto;
import com.redbus.booking.dto.BookingResponseDto;
import com.redbus.booking.dto.PaymentCallbackDto;
import com.redbus.booking.enums.BookingStatus;
import com.redbus.booking.service.BookingService;
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
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<BookingResponseDto>> initiateBooking(
            @Valid @RequestBody BookingRequestDto requestDto) {
        log.info("Initiating booking for journey: {}", requestDto.getJourneyReferenceId());
        BookingResponseDto booking = bookingService.initiateBooking(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(booking, "Booking initiated successfully"));
    }
    
    @PostMapping("/{referenceId}/confirm")
    public ResponseEntity<ApiResponse<BookingResponseDto>> confirmBooking(
            @PathVariable UUID referenceId,
            @Valid @RequestBody PaymentCallbackDto paymentCallback) {
        log.info("Confirming booking: {}", referenceId);
        BookingResponseDto booking = bookingService.confirmBooking(referenceId, paymentCallback);
        return ResponseEntity.ok(ApiResponse.success(booking, "Booking confirmed successfully"));
    }
    
    @PostMapping("/{referenceId}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDto>> cancelBooking(
            @PathVariable UUID referenceId,
            @RequestParam(required = false) String reason) {
        log.info("Cancelling booking: {}", referenceId);
        BookingResponseDto booking = bookingService.cancelBooking(referenceId, reason);
        return ResponseEntity.ok(ApiResponse.success(booking, "Booking cancelled successfully"));
    }
    
    @GetMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> getBooking(@PathVariable UUID referenceId) {
        log.info("Fetching booking: {}", referenceId);
        BookingResponseDto booking = bookingService.getBookingById(referenceId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @GetMapping("/code/{bookingCode}")
    public ResponseEntity<ApiResponse<BookingResponseDto>> getBookingByCode(@PathVariable String bookingCode) {
        log.info("Fetching booking by code: {}", bookingCode);
        BookingResponseDto booking = bookingService.getBookingByCode(bookingCode);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }
    
    @GetMapping("/user/{userReferenceId}")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getUserBookings(
            @PathVariable String userReferenceId) {
        log.info("Fetching bookings for user: {}", userReferenceId);
        List<BookingResponseDto> bookings = bookingService.getUserBookings(userReferenceId);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    @GetMapping("/journey/{journeyReferenceId}")
    public ResponseEntity<ApiResponse<List<BookingResponseDto>>> getJourneyBookings(
            @PathVariable String journeyReferenceId) {
        log.info("Fetching bookings for journey: {}", journeyReferenceId);
        List<BookingResponseDto> bookings = bookingService.getJourneyBookings(journeyReferenceId);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }
    
    @PostMapping("/{referenceId}/status")
    public ResponseEntity<ApiResponse<Void>> updateBookingStatus(
            @PathVariable UUID referenceId,
            @RequestParam BookingStatus status) {
        log.info("Updating booking {} status to {}", referenceId, status);
        bookingService.updateBookingStatus(referenceId, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Booking status updated successfully"));
    }
    
    @PostMapping("/process-expired")
    public ResponseEntity<ApiResponse<Void>> processExpiredBookings() {
        log.info("Processing expired bookings");
        bookingService.processExpiredBookings();
        return ResponseEntity.ok(ApiResponse.success(null, "Expired bookings processed"));
    }
    
    @GetMapping("/check-availability")
    public ResponseEntity<ApiResponse<Boolean>> checkSeatAvailability(
            @RequestParam UUID journeyReferenceId,
            @RequestParam List<UUID> seatInventoryIds) {
        log.info("Checking seat availability for journey: {}", journeyReferenceId);
        boolean available = bookingService.checkSeatAvailability(journeyReferenceId, seatInventoryIds);
        return ResponseEntity.ok(ApiResponse.success(available));
    }
}
