package com.redbus.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerResponseDto {
    private UUID referenceId;
    private String seatNumber;
    private String passengerName;
    private Integer age;
    private String gender;
    private String idType;
    private String idNumber;
    private Boolean isPrimary;
}
