package com.redbus.journey.repository;

import com.redbus.journey.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
    
    Optional<Stop> findByReferenceId(UUID referenceId);
    
    Optional<Stop> findByStopNameAndCityAndState(String stopName, String city, String state);
    
    List<Stop> findByCity(String city);
    
    List<Stop> findByState(String state);
    
    List<Stop> findByIsActiveTrue();
    
    boolean existsByStopNameAndCityAndState(String stopName, String city, String state);
}
