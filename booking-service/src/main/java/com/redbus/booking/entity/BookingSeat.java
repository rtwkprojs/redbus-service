package com.redbus.booking.entity;

import com.redbus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "booking_seats",
    uniqueConstraints = @UniqueConstraint(columnNames = {"booking_id", "seat_inventory_reference_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeat extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @Column(name = "seat_inventory_reference_id", nullable = false)
    private String seatInventoryReferenceId;
    
    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;
    
    @Column(name = "passenger_reference_id", nullable = false)
    private String passengerReferenceId;
    
    @Column(name = "seat_fare", nullable = false)
    private Double seatFare;
    
    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;
}
