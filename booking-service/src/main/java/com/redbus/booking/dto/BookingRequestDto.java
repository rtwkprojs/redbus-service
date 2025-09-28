package com.redbus.booking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    
    @NotNull(message = "Journey reference ID is required")
    private UUID journeyReferenceId;
    
    @NotNull(message = "User reference ID is required")
    private String userReferenceId;
    
    private UUID boardingPointReferenceId;
    
    private UUID droppingPointReferenceId;
    
    @NotNull(message = "Seat selections are required")
    @Size(min = 1, max = 6, message = "You can book between 1 to 6 seats")
    @Valid
    private List<SeatSelectionDto> seatSelections;
    
    @NotNull(message = "Passenger details are required")
    @Valid
    private List<PassengerDto> passengers;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String contactPhone;
    
    private String promoCode;
}
