package com.redbus.agency.repository;

import com.redbus.agency.entity.Vehicle;
import com.redbus.agency.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    Optional<Vehicle> findByReferenceId(UUID referenceId);
    
    boolean existsByReferenceId(UUID referenceId);
    
    boolean existsByRegistrationNumber(String registrationNumber);
    
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    
    List<Vehicle> findByAgency_ReferenceId(UUID agencyReferenceId);
    
    List<Vehicle> findByAgency_ReferenceIdAndIsActiveTrue(UUID agencyReferenceId);
    
    List<Vehicle> findByVehicleType(VehicleType vehicleType);
    
    @Query("SELECT v FROM Vehicle v WHERE v.agency.id = :agencyId AND v.isActive = true")
    List<Vehicle> findActiveVehiclesByAgencyId(@Param("agencyId") Long agencyId);
    
    @Query("SELECT v FROM Vehicle v WHERE v.referenceId = :referenceId AND v.agency.referenceId = :agencyReferenceId")
    Optional<Vehicle> findByReferenceIdAndAgency(@Param("referenceId") UUID referenceId, @Param("agencyReferenceId") UUID agencyReferenceId);
}
