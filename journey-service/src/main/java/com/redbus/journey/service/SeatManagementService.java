package com.redbus.journey.service;

import com.redbus.journey.dto.SeatInventoryDto;

import java.util.List;
import java.util.UUID;

public interface SeatManagementService {
    
    /**
     * Lock seats for booking with pessimistic locking
     */
    List<SeatInventoryDto> lockSeatsForBooking(UUID journeyReferenceId, List<UUID> seatInventoryIds);
    
    /**
     * Update seat booking status after payment
     */
    void updateSeatBookingStatus(UUID journeyReferenceId, List<UUID> seatInventoryIds, String bookingReferenceId);
    
    /**
     * Release seats (for cancellation or expiry)
     */
    void releaseSeats(UUID journeyReferenceId, List<UUID> seatInventoryIds);
    
    /**
     * Get seat inventory for specific seats
     */
    List<SeatInventoryDto> getSeatInventory(UUID journeyReferenceId, List<UUID> seatInventoryIds);
    
    /**
     * Check if seats are available
     */
    boolean checkSeatAvailability(UUID journeyReferenceId, List<UUID> seatInventoryIds);
}
