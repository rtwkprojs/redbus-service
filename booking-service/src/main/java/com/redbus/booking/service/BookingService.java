package com.redbus.booking.service;

import com.redbus.booking.dto.BookingRequestDto;
import com.redbus.booking.dto.BookingResponseDto;
import com.redbus.booking.dto.PaymentCallbackDto;
import com.redbus.booking.enums.BookingStatus;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    
    /**
     * Initiate a new booking with seat blocking
     */
    BookingResponseDto initiateBooking(BookingRequestDto requestDto);
    
    /**
     * Confirm booking after payment success
     */
    BookingResponseDto confirmBooking(UUID bookingReferenceId, PaymentCallbackDto paymentCallback);
    
    /**
     * Cancel a booking
     */
    BookingResponseDto cancelBooking(UUID bookingReferenceId, String reason);
    
    /**
     * Get booking by reference ID
     */
    BookingResponseDto getBookingById(UUID referenceId);
    
    /**
     * Get booking by booking code
     */
    BookingResponseDto getBookingByCode(String bookingCode);
    
    /**
     * Get all bookings for a user
     */
    List<BookingResponseDto> getUserBookings(String userReferenceId);
    
    /**
     * Get bookings for a journey
     */
    List<BookingResponseDto> getJourneyBookings(String journeyReferenceId);
    
    /**
     * Update booking status
     */
    void updateBookingStatus(UUID bookingReferenceId, BookingStatus status);
    
    /**
     * Process expired bookings
     */
    void processExpiredBookings();
    
    /**
     * Release blocked seats for expired/cancelled bookings
     */
    void releaseBlockedSeats(UUID bookingReferenceId);
    
    /**
     * Check seat availability
     */
    boolean checkSeatAvailability(UUID journeyReferenceId, List<UUID> seatInventoryIds);
}
