package com.redbus.agency.entity;

import com.redbus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {
    
    @Column(name = "registration_number", unique = true, nullable = false, length = 20)
    private String registrationNumber;
    
    @Column(name = "vehicle_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    @Column(name = "manufacturer", length = 50)
    private String manufacturer;
    
    @Column(name = "model", length = 50)
    private String model;
    
    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "has_ac", nullable = false)
    private Boolean hasAC = false;
    
    @Column(name = "has_wifi", nullable = false)
    private Boolean hasWifi = false;
    
    @Column(name = "has_charging_points", nullable = false)
    private Boolean hasChargingPoints = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;
}
