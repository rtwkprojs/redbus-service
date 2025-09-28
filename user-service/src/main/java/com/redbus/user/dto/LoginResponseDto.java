package com.redbus.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserResponseDto user;
}
