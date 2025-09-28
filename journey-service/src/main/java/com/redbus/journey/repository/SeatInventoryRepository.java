package com.redbus.journey.repository;

import com.redbus.journey.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {
    
    Optional<SeatInventory> findByReferenceId(UUID referenceId);
    
    List<SeatInventory> findByJourneyId(Long journeyId);
    
    List<SeatInventory> findByJourneyIdAndIsAvailableTrue(Long journeyId);
    
    Optional<SeatInventory> findByJourneyIdAndSeatNumber(Long journeyId, String seatNumber);
    
    @Query("SELECT COUNT(s) FROM SeatInventory s WHERE s.journey.id = :journeyId AND s.isAvailable = true")
    Integer countAvailableSeats(@Param("journeyId") Long journeyId);
    
    @Query("SELECT s FROM SeatInventory s WHERE s.journey.id = :journeyId ORDER BY s.seatNumber")
    List<SeatInventory> findByJourneyIdOrderBySeatNumber(@Param("journeyId") Long journeyId);
    
    List<SeatInventory> findByBookingReferenceId(String bookingReferenceId);
}
