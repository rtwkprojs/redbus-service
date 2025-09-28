package com.redbus.journey.repository;

import com.redbus.journey.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    Optional<Route> findByReferenceId(UUID referenceId);
    
    List<Route> findByAgencyReferenceId(String agencyReferenceId);
    
    List<Route> findBySourceCityAndDestinationCity(String sourceCity, String destinationCity);
    
    List<Route> findByIsActiveTrue();
    
    @Query("SELECT r FROM Route r WHERE r.agencyReferenceId = :agencyId AND r.isActive = true")
    List<Route> findActiveRoutesByAgency(@Param("agencyId") String agencyReferenceId);
    
    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.routeStops rs LEFT JOIN FETCH rs.stop WHERE r.referenceId = :referenceId")
    Optional<Route> findByReferenceIdWithStops(@Param("referenceId") UUID referenceId);
    
    boolean existsByRouteNameAndAgencyReferenceId(String routeName, String agencyReferenceId);
}
