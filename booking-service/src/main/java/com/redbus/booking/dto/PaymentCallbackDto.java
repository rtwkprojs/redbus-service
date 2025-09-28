package com.redbus.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackDto {
    
    @NotBlank(message = "Payment reference ID is required")
    private String paymentReferenceId;
    
    @NotBlank(message = "Payment status is required")
    private String paymentStatus; // SUCCESS, FAILED, PENDING
    
    @NotNull(message = "Payment amount is required")
    private Double paymentAmount;
    
    private String paymentMethod; // CARD, UPI, NET_BANKING, WALLET
    
    private LocalDateTime paymentTime;
    
    private String transactionId;
    
    private String failureReason;
}
