package com.redbus.payment.service;

import com.redbus.payment.dto.PaymentRequestDto;
import com.redbus.payment.dto.PaymentResponseDto;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    
    /**
     * Process a payment - Simple validation: amount entered must match amount required
     */
    PaymentResponseDto processPayment(PaymentRequestDto requestDto);
    
    /**
     * Get payment by reference ID
     */
    PaymentResponseDto getPaymentById(UUID referenceId);
    
    /**
     * Get payment by payment code
     */
    PaymentResponseDto getPaymentByCode(String paymentCode);
    
    /**
     * Get payments for a booking
     */
    List<PaymentResponseDto> getPaymentsByBooking(String bookingReferenceId);
    
    /**
     * Get payments for a user
     */
    List<PaymentResponseDto> getPaymentsByUser(String userReferenceId);
    
    /**
     * Check payment status
     */
    String checkPaymentStatus(UUID referenceId);
    
    /**
     * Initiate refund (mock)
     */
    PaymentResponseDto initiateRefund(UUID paymentReferenceId, Double refundAmount);
}
