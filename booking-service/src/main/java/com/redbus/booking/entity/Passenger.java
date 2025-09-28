package com.redbus.booking.entity;

import com.redbus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Passenger extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;
    
    @Column(name = "passenger_name", nullable = false, length = 100)
    private String passengerName;
    
    @Column(name = "age", nullable = false)
    private Integer age;
    
    @Column(name = "gender", nullable = false, length = 10)
    private String gender;
    
    @Column(name = "id_type", length = 20)
    private String idType;
    
    @Column(name = "id_number", length = 50)
    private String idNumber;
    
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;
}
