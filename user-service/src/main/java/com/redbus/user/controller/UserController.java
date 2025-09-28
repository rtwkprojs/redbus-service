package com.redbus.user.controller;

import com.redbus.common.dto.ApiResponse;
import com.redbus.user.dto.*;
import com.redbus.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto) {
        log.info("Received registration request for username: {}", registrationDto.getUsername());
        UserResponseDto user = userService.registerUser(registrationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "User registered successfully"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @Valid @RequestBody UserLoginDto loginDto) {
        log.info("Login request received for: {}", loginDto.getUsernameOrEmail());
        LoginResponseDto response = userService.login(loginDto);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
    
    @GetMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByReferenceId(
            @PathVariable UUID referenceId) {
        log.info("Fetching user with referenceId: {}", referenceId);
        UserResponseDto user = userService.getUserByReferenceId(referenceId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserByUsername(
            @PathVariable String username) {
        log.info("Fetching user with username: {}", username);
        UserResponseDto user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PutMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable UUID referenceId,
            @Valid @RequestBody UserUpdateDto updateDto) {
        log.info("Updating user with referenceId: {}", referenceId);
        UserResponseDto user = userService.updateUser(referenceId, updateDto);
        return ResponseEntity.ok(ApiResponse.success(user, "User updated successfully"));
    }
    
    @PostMapping("/{referenceId}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable UUID referenceId,
            @Valid @RequestBody PasswordChangeDto passwordChangeDto) {
        log.info("Password change request for user: {}", referenceId);
        userService.changePassword(referenceId, passwordChangeDto);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }
    
    @DeleteMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID referenceId) {
        log.info("Delete request for user: {}", referenceId);
        userService.deleteUser(referenceId);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
    
    @GetMapping("/check-username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
    
    @GetMapping("/check-email/{email}")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
