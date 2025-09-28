package com.redbus.booking.entity;

import com.redbus.common.entity.BaseEntity;
import com.redbus.booking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseEntity {
    
    @Column(name = "booking_code", unique = true, nullable = false, length = 20)
    private String bookingCode;
    
    @Column(name = "user_reference_id", nullable = false)
    private String userReferenceId;
    
    @Column(name = "journey_reference_id", nullable = false)
    private String journeyReferenceId;
    
    @Column(name = "boarding_point_reference_id")
    private String boardingPointReferenceId;
    
    @Column(name = "dropping_point_reference_id")
    private String droppingPointReferenceId;
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    
    @Column(name = "discount_amount")
    private Double discountAmount = 0.0;
    
    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;
    
    @Column(name = "booking_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus = BookingStatus.INITIATED;
    
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "PENDING";
    
    @Column(name = "payment_reference_id")
    private String paymentReferenceId;
    
    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;
    
    @Column(name = "expiry_time", nullable = false)
    private LocalDateTime expiryTime;
    
    @Column(name = "cancellation_time")
    private LocalDateTime cancellationTime;
    
    @Column(name = "cancellation_reason")
    private String cancellationReason;
    
    @Column(name = "refund_amount")
    private Double refundAmount;
    
    @Column(name = "contact_email", nullable = false)
    private String contactEmail;
    
    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Passenger> passengers = new ArrayList<>();
    
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingSeat> bookingSeats = new ArrayList<>();
}
