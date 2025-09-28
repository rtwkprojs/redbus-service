package com.redbus.user.service.impl;

import com.redbus.common.exception.BusinessException;
import com.redbus.common.exception.ResourceNotFoundException;
import com.redbus.common.exception.ValidationException;
import com.redbus.user.dto.*;
import com.redbus.user.entity.User;
import com.redbus.user.mapper.UserMapper;
import com.redbus.user.repository.UserRepository;
import com.redbus.user.security.JwtService;
import com.redbus.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    
    @Override
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering new user with username: {}", registrationDto.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new ValidationException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setPhone(registrationDto.getPhone());
        user.setUserType(registrationDto.getUserType());
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getReferenceId());
        
        return userMapper.toResponseDto(savedUser);
    }
    
    @Override
    public LoginResponseDto login(UserLoginDto loginDto) {
        log.info("User login attempt with: {}", loginDto.getUsernameOrEmail());
        
        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(
                loginDto.getUsernameOrEmail(), 
                loginDto.getUsernameOrEmail()
        ).orElseThrow(() -> new BusinessException("Invalid credentials"));
        
        // Check password
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password attempt for user: {}", loginDto.getUsernameOrEmail());
            throw new BusinessException("Invalid credentials");
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(user);
        Long expiresIn = jwtService.getExpirationTime();
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        return LoginResponseDto.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(userMapper.toResponseDto(user))
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByReferenceId(UUID referenceId) {
        User user = userRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "referenceId", referenceId));
        return userMapper.toResponseDto(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toResponseDto(user);
    }
    
    @Override
    public UserResponseDto updateUser(UUID referenceId, UserUpdateDto updateDto) {
        log.info("Updating user with referenceId: {}", referenceId);
        
        User user = userRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "referenceId", referenceId));
        
        // Update email if provided and different
        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new ValidationException("Email already exists");
            }
            user.setEmail(updateDto.getEmail());
        }
        
        // Update phone if provided
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", referenceId);
        
        return userMapper.toResponseDto(updatedUser);
    }
    
    @Override
    public void changePassword(UUID referenceId, PasswordChangeDto passwordChangeDto) {
        log.info("Changing password for user: {}", referenceId);
        
        User user = userRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "referenceId", referenceId));
        
        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect");
        }
        
        // Check if new password matches confirm password
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new ValidationException("New password and confirm password do not match");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password changed successfully for user: {}", referenceId);
    }
    
    @Override
    public void deleteUser(UUID referenceId) {
        log.info("Deleting user with referenceId: {}", referenceId);
        
        User user = userRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "referenceId", referenceId));
        
        userRepository.delete(user);
        log.info("User deleted successfully: {}", referenceId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
