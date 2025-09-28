package com.redbus.booking.repository;

import com.redbus.booking.entity.Booking;
import com.redbus.booking.enums.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByReferenceId(UUID referenceId);
    
    Optional<Booking> findByBookingCode(String bookingCode);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b WHERE b.referenceId = :referenceId")
    Optional<Booking> findByReferenceIdWithLock(@Param("referenceId") UUID referenceId);
    
    List<Booking> findByUserReferenceId(String userReferenceId);
    
    List<Booking> findByJourneyReferenceId(String journeyReferenceId);
    
    List<Booking> findByBookingStatus(BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.bookingStatus = :status AND b.expiryTime < :currentTime")
    List<Booking> findExpiredBookings(@Param("status") BookingStatus status, 
                                      @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.journeyReferenceId = :journeyId AND b.bookingStatus IN ('CONFIRMED', 'SEATS_BLOCKED')")
    Integer countConfirmedSeats(@Param("journeyId") String journeyReferenceId);
    
    @Query("SELECT b FROM Booking b WHERE b.userReferenceId = :userId AND b.bookingStatus = 'CONFIRMED' ORDER BY b.bookingTime DESC")
    List<Booking> findUserConfirmedBookings(@Param("userId") String userReferenceId);
    
    boolean existsByBookingCode(String bookingCode);
}
