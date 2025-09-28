package com.redbus.agency.entity;

import com.redbus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agency extends BaseEntity {
    
    @Column(name = "agency_name", nullable = false, length = 100)
    private String agencyName;
    
    @Column(name = "contact_email", nullable = false, length = 100)
    private String contactEmail;
    
    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;
    
    @Column(name = "address", length = 200)
    private String address;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "owner_reference_id", nullable = false)
    private String ownerReferenceId;  // User reference_id who owns this agency
    
    @OneToMany(mappedBy = "agency", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vehicle> vehicles = new ArrayList<>();
}
