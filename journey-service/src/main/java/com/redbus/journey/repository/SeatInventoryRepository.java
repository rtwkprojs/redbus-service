package com.redbus.journey.repository;

import com.redbus.journey.entity.SeatInventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatInventoryRepository extends JpaRepository<SeatInventory, Long> {
    
    Optional<SeatInventory> findByReferenceId(UUID referenceId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatInventory s WHERE s.referenceId = :referenceId")
    Optional<SeatInventory> findByReferenceIdWithLock(@Param("referenceId") UUID referenceId);
    
    List<SeatInventory> findByJourneyId(Long journeyId);
    
    List<SeatInventory> findByJourneyIdAndIsAvailableTrue(Long journeyId);
    
    Optional<SeatInventory> findByJourneyIdAndSeatNumber(Long journeyId, String seatNumber);
    
    @Query("SELECT COUNT(s) FROM SeatInventory s WHERE s.journey.id = :journeyId AND s.isAvailable = true")
    Integer countAvailableSeats(@Param("journeyId") Long journeyId);
    
    @Query("SELECT s FROM SeatInventory s WHERE s.journey.id = :journeyId ORDER BY s.seatNumber")
    List<SeatInventory> findByJourneyIdOrderBySeatNumber(@Param("journeyId") Long journeyId);
    
    List<SeatInventory> findByBookingReferenceId(String bookingReferenceId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatInventory s WHERE s.referenceId IN :referenceIds")
    List<SeatInventory> findByReferenceIdsWithLock(@Param("referenceIds") List<UUID> referenceIds);
    
    @Modifying
    @Query("UPDATE SeatInventory s SET s.isAvailable = :available, s.bookingReferenceId = :bookingRef WHERE s.referenceId IN :referenceIds")
    void updateSeatAvailability(@Param("referenceIds") List<UUID> referenceIds, 
                               @Param("available") Boolean available, 
                               @Param("bookingRef") String bookingReferenceId);
}
