package com.redbus.user.service;

import com.redbus.user.dto.*;
import com.redbus.user.entity.User;

import java.util.UUID;

public interface UserService {
    
    UserResponseDto registerUser(UserRegistrationDto registrationDto);
    
    LoginResponseDto login(UserLoginDto loginDto);
    
    UserResponseDto getUserByReferenceId(UUID referenceId);
    
    UserResponseDto getUserByUsername(String username);
    
    UserResponseDto updateUser(UUID referenceId, UserUpdateDto updateDto);
    
    void changePassword(UUID referenceId, PasswordChangeDto passwordChangeDto);
    
    void deleteUser(UUID referenceId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
