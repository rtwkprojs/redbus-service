package com.redbus.payment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {
    
    @NotNull(message = "Booking reference ID is required")
    private UUID bookingReferenceId;
    
    @NotBlank(message = "User reference ID is required")
    private String userReferenceId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    @DecimalMax(value = "100000.0", message = "Amount cannot exceed 100000")
    private Double amountRequired;
    
    @NotNull(message = "Entered amount is required")
    @DecimalMin(value = "0.0", message = "Entered amount cannot be negative")
    private Double amountEntered;
    
    private String paymentMethod = "MOCK"; // MOCK, CARD, UPI, NET_BANKING, WALLET
}
