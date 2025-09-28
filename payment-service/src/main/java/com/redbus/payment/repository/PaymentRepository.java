package com.redbus.payment.repository;

import com.redbus.payment.entity.Payment;
import com.redbus.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByReferenceId(UUID referenceId);
    
    Optional<Payment> findByPaymentCode(String paymentCode);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByBookingReferenceId(String bookingReferenceId);
    
    List<Payment> findByUserReferenceId(String userReferenceId);
    
    List<Payment> findByPaymentStatus(PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.bookingReferenceId = :bookingId AND p.paymentStatus = 'SUCCESS'")
    Optional<Payment> findSuccessfulPaymentForBooking(@Param("bookingId") String bookingReferenceId);
    
    boolean existsByPaymentCode(String paymentCode);
}
