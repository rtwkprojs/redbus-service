package com.redbus.payment.service.impl;

import com.redbus.common.exception.BusinessException;
import com.redbus.common.exception.ResourceNotFoundException;
import com.redbus.payment.dto.PaymentRequestDto;
import com.redbus.payment.dto.PaymentResponseDto;
import com.redbus.payment.entity.Payment;
import com.redbus.payment.enums.PaymentStatus;
import com.redbus.payment.repository.PaymentRepository;
import com.redbus.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    private static final String PAYMENT_CODE_PREFIX = "PAY";
    private static final double TOLERANCE = 0.01; // Allow 1 paisa tolerance for floating point comparison
    
    @Override
    public PaymentResponseDto processPayment(PaymentRequestDto requestDto) {
        log.info("Processing payment for booking: {} with amount: {}", 
                requestDto.getBookingReferenceId(), requestDto.getAmountRequired());
        
        // Check if payment already exists for this booking
        paymentRepository.findSuccessfulPaymentForBooking(requestDto.getBookingReferenceId().toString())
                .ifPresent(p -> {
                    throw new BusinessException("Payment already completed for this booking");
                });
        
        // Create payment record
        Payment payment = new Payment();
        payment.setPaymentCode(generatePaymentCode());
        payment.setBookingReferenceId(requestDto.getBookingReferenceId().toString());
        payment.setUserReferenceId(requestDto.getUserReferenceId());
        payment.setAmountRequired(requestDto.getAmountRequired());
        payment.setAmountEntered(requestDto.getAmountEntered());
        payment.setPaymentMethod(requestDto.getPaymentMethod());
        payment.setPaymentStatus(PaymentStatus.PROCESSING);
        
        payment = paymentRepository.save(payment);
        
        // Simple validation: Check if entered amount matches required amount
        boolean isValidAmount = Math.abs(requestDto.getAmountEntered() - requestDto.getAmountRequired()) < TOLERANCE;
        
        if (isValidAmount) {
            // Payment successful
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(generateTransactionId());
            payment.setPaymentTime(LocalDateTime.now());
            log.info("Payment successful for booking: {}", requestDto.getBookingReferenceId());
        } else {
            // Payment failed
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(String.format(
                    "Amount mismatch. Required: %.2f, Entered: %.2f", 
                    requestDto.getAmountRequired(), 
                    requestDto.getAmountEntered()
            ));
            payment.setRetryCount(payment.getRetryCount() + 1);
            log.warn("Payment failed for booking: {} - Amount mismatch", requestDto.getBookingReferenceId());
        }
        
        payment = paymentRepository.save(payment);
        
        // Call booking service to update booking status
        callBookingServiceCallback(payment);
        
        return toPaymentResponseDto(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(UUID referenceId) {
        log.info("Fetching payment: {}", referenceId);
        
        Payment payment = paymentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        return toPaymentResponseDto(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByCode(String paymentCode) {
        log.info("Fetching payment by code: {}", paymentCode);
        
        Payment payment = paymentRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        return toPaymentResponseDto(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByBooking(String bookingReferenceId) {
        log.info("Fetching payments for booking: {}", bookingReferenceId);
        
        return paymentRepository.findByBookingReferenceId(bookingReferenceId).stream()
                .map(this::toPaymentResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByUser(String userReferenceId) {
        log.info("Fetching payments for user: {}", userReferenceId);
        
        return paymentRepository.findByUserReferenceId(userReferenceId).stream()
                .map(this::toPaymentResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public String checkPaymentStatus(UUID referenceId) {
        log.info("Checking payment status: {}", referenceId);
        
        Payment payment = paymentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        return payment.getPaymentStatus().name();
    }
    
    @Override
    public PaymentResponseDto initiateRefund(UUID paymentReferenceId, Double refundAmount) {
        log.info("Initiating refund for payment: {} with amount: {}", paymentReferenceId, refundAmount);
        
        Payment payment = paymentRepository.findByReferenceId(paymentReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        if (payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Can only refund successful payments");
        }
        
        if (refundAmount > payment.getAmountRequired()) {
            throw new BusinessException("Refund amount cannot exceed payment amount");
        }
        
        // Mock refund - always successful
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setFailureReason("Refunded: " + refundAmount);
        payment = paymentRepository.save(payment);
        
        log.info("Refund initiated successfully for payment: {}", paymentReferenceId);
        
        return toPaymentResponseDto(payment);
    }
    
    private String generatePaymentCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("%s%s%s", PAYMENT_CODE_PREFIX, datePart, uniquePart);
    }
    
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    private void callBookingServiceCallback(Payment payment) {
        // In a real implementation, this would call the Booking Service
        // For now, we'll just log it
        log.info("Calling booking service callback for payment: {} with status: {}", 
                payment.getPaymentCode(), payment.getPaymentStatus());
    }
    
    private PaymentResponseDto toPaymentResponseDto(Payment payment) {
        return PaymentResponseDto.builder()
                .referenceId(payment.getReferenceId())
                .paymentCode(payment.getPaymentCode())
                .bookingReferenceId(UUID.fromString(payment.getBookingReferenceId()))
                .userReferenceId(payment.getUserReferenceId())
                .amountRequired(payment.getAmountRequired())
                .amountEntered(payment.getAmountEntered())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .paymentTime(payment.getPaymentTime())
                .failureReason(payment.getFailureReason())
                .retryCount(payment.getRetryCount())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
