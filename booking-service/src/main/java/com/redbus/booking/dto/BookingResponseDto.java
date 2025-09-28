package com.redbus.booking.dto;

import com.redbus.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private UUID referenceId;
    private String bookingCode;
    private String userReferenceId;
    private UUID journeyReferenceId;
    private String journeyCode;
    private String sourceCity;
    private String destinationCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private UUID boardingPointReferenceId;
    private String boardingPointName;
    private UUID droppingPointReferenceId;
    private String droppingPointName;
    private Integer totalSeats;
    private Double totalAmount;
    private Double discountAmount;
    private Double finalAmount;
    private BookingStatus bookingStatus;
    private String paymentStatus;
    private String paymentReferenceId;
    private LocalDateTime bookingTime;
    private LocalDateTime expiryTime;
    private String contactEmail;
    private String contactPhone;
    private List<PassengerResponseDto> passengers;
    private List<BookingSeatDto> seats;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
