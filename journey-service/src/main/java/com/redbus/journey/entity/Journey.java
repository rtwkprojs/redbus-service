package com.redbus.journey.entity;

import com.redbus.common.entity.BaseEntity;
import com.redbus.journey.enums.JourneyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journeys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Journey extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @Column(name = "journey_code", unique = true, nullable = false, length = 20)
    private String journeyCode;
    
    @Column(name = "vehicle_reference_id", nullable = false)
    private String vehicleReferenceId;
    
    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;
    
    @Column(name = "journey_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JourneyStatus journeyStatus = JourneyStatus.SCHEDULED;
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;
    
    @Column(name = "base_fare", nullable = false)
    private Double baseFare;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "amenities", length = 500)
    private String amenities; // JSON string of amenities
    
    @OneToMany(mappedBy = "journey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SeatInventory> seatInventory = new ArrayList<>();
}
