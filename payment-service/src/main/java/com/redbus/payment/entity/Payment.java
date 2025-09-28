package com.redbus.payment.entity;

import com.redbus.common.entity.BaseEntity;
import com.redbus.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {
    
    @Column(name = "payment_code", unique = true, nullable = false, length = 20)
    private String paymentCode;
    
    @Column(name = "booking_reference_id", nullable = false)
    private String bookingReferenceId;
    
    @Column(name = "user_reference_id", nullable = false)
    private String userReferenceId;
    
    @Column(name = "amount_required", nullable = false)
    private Double amountRequired;
    
    @Column(name = "amount_entered")
    private Double amountEntered;
    
    @Column(name = "payment_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_method", length = 20)
    private String paymentMethod = "MOCK";
    
    @Column(name = "transaction_id", unique = true)
    private String transactionId;
    
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;
    
    @Column(name = "failure_reason")
    private String failureReason;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
}
