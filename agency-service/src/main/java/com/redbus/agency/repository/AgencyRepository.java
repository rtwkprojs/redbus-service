package com.redbus.agency.repository;

import com.redbus.agency.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {
    
    Optional<Agency> findByReferenceId(UUID referenceId);
    
    boolean existsByReferenceId(UUID referenceId);
    
    boolean existsByContactEmail(String email);
    
    boolean existsByContactPhone(String phone);
    
    Optional<Agency> findByAgencyNameIgnoreCase(String agencyName);
    
    List<Agency> findByOwnerReferenceId(String ownerReferenceId);
    
    List<Agency> findByIsActiveTrue();
    
    @Query("SELECT a FROM Agency a WHERE a.referenceId = :referenceId AND a.ownerReferenceId = :ownerReferenceId")
    Optional<Agency> findByReferenceIdAndOwner(@Param("referenceId") UUID referenceId, @Param("ownerReferenceId") String ownerReferenceId);
}
