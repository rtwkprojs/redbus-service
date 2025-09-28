package com.redbus.user.dto;

import com.redbus.user.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    
    private UUID referenceId;
    private String username;
    private String email;
    private String phone;
    private UserType userType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
