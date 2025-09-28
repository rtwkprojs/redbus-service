package com.redbus.journey.repository;

import com.redbus.journey.entity.Journey;
import com.redbus.journey.enums.JourneyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {
    
    Optional<Journey> findByReferenceId(UUID referenceId);
    
    Optional<Journey> findByJourneyCode(String journeyCode);
    
    List<Journey> findByRouteId(Long routeId);
    
    List<Journey> findByVehicleReferenceId(String vehicleReferenceId);
    
    List<Journey> findByJourneyStatus(JourneyStatus status);
    
    @Query("SELECT j FROM Journey j WHERE j.route.id = :routeId AND j.departureTime BETWEEN :startTime AND :endTime")
    List<Journey> findByRouteAndDepartureTimeBetween(
            @Param("routeId") Long routeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT j FROM Journey j WHERE j.departureTime >= :departureTime AND j.isActive = true ORDER BY j.departureTime")
    List<Journey> findUpcomingJourneys(@Param("departureTime") LocalDateTime departureTime);
    
    @Query("SELECT j FROM Journey j JOIN j.route r WHERE r.sourceCity = :sourceCity AND r.destinationCity = :destinationCity AND DATE(j.departureTime) = DATE(:travelDate)")
    List<Journey> findBySourceDestinationAndDate(
            @Param("sourceCity") String sourceCity,
            @Param("destinationCity") String destinationCity,
            @Param("travelDate") LocalDateTime travelDate);
    
    boolean existsByJourneyCode(String journeyCode);
    
    @Query("SELECT j FROM Journey j WHERE j.vehicleReferenceId = :vehicleId AND j.departureTime BETWEEN :startTime AND :endTime")
    List<Journey> findVehicleSchedule(
            @Param("vehicleId") String vehicleReferenceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
