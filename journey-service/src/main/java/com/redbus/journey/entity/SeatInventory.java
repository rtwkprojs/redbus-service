package com.redbus.journey.entity;

import com.redbus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "seat_inventory",
    uniqueConstraints = @UniqueConstraint(columnNames = {"journey_id", "seat_number"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatInventory extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journey_id", nullable = false)
    private Journey journey;
    
    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;
    
    @Column(name = "seat_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SeatType seatType;
    
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;
    
    @Column(name = "is_ladies_seat", nullable = false)
    private Boolean isLadiesSeat = false;
    
    @Column(name = "fare_multiplier", nullable = false)
    private Double fareMultiplier = 1.0; // For premium seats
    
    @Column(name = "booking_reference_id")
    private String bookingReferenceId; // Reference to booking when seat is booked
}
