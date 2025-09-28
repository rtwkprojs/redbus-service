package com.redbus.payment.controller;

import com.redbus.common.dto.ApiResponse;
import com.redbus.payment.dto.PaymentRequestDto;
import com.redbus.payment.dto.PaymentResponseDto;
import com.redbus.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> processPayment(
            @Valid @RequestBody PaymentRequestDto requestDto) {
        log.info("Processing payment for booking: {}", requestDto.getBookingReferenceId());
        PaymentResponseDto payment = paymentService.processPayment(requestDto);
        
        String message = payment.getPaymentStatus().name().equals("SUCCESS") 
                ? "Payment processed successfully" 
                : "Payment failed: " + payment.getFailureReason();
        
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(payment, message));
    }
    
    @GetMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPayment(@PathVariable UUID referenceId) {
        log.info("Fetching payment: {}", referenceId);
        PaymentResponseDto payment = paymentService.getPaymentById(referenceId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }
    
    @GetMapping("/code/{paymentCode}")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPaymentByCode(@PathVariable String paymentCode) {
        log.info("Fetching payment by code: {}", paymentCode);
        PaymentResponseDto payment = paymentService.getPaymentByCode(paymentCode);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }
    
    @GetMapping("/booking/{bookingReferenceId}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getPaymentsByBooking(
            @PathVariable String bookingReferenceId) {
        log.info("Fetching payments for booking: {}", bookingReferenceId);
        List<PaymentResponseDto> payments = paymentService.getPaymentsByBooking(bookingReferenceId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
    
    @GetMapping("/user/{userReferenceId}")
    public ResponseEntity<ApiResponse<List<PaymentResponseDto>>> getPaymentsByUser(
            @PathVariable String userReferenceId) {
        log.info("Fetching payments for user: {}", userReferenceId);
        List<PaymentResponseDto> payments = paymentService.getPaymentsByUser(userReferenceId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
    
    @GetMapping("/{referenceId}/status")
    public ResponseEntity<ApiResponse<String>> checkPaymentStatus(@PathVariable UUID referenceId) {
        log.info("Checking payment status: {}", referenceId);
        String status = paymentService.checkPaymentStatus(referenceId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }
    
    @PostMapping("/{referenceId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> initiateRefund(
            @PathVariable UUID referenceId,
            @RequestBody Map<String, Double> request) {
        log.info("Initiating refund for payment: {}", referenceId);
        Double refundAmount = request.get("refundAmount");
        PaymentResponseDto payment = paymentService.initiateRefund(referenceId, refundAmount);
        return ResponseEntity.ok(ApiResponse.success(payment, "Refund initiated successfully"));
    }
}
