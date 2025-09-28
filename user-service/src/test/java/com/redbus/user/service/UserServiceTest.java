package com.redbus.user.service;

import com.redbus.common.exception.BusinessException;
import com.redbus.common.exception.ValidationException;
import com.redbus.user.dto.LoginResponseDto;
import com.redbus.user.dto.UserLoginDto;
import com.redbus.user.dto.UserRegistrationDto;
import com.redbus.user.dto.UserResponseDto;
import com.redbus.user.entity.User;
import com.redbus.user.enums.UserType;
import com.redbus.user.mapper.UserMapper;
import com.redbus.user.repository.UserRepository;
import com.redbus.user.security.JwtService;
import com.redbus.user.service.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserMapper userMapper;
    
    @Mock
    private JwtService jwtService;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private UserRegistrationDto registrationDto;
    private User user;
    private UserResponseDto userResponseDto;
    
    @BeforeEach
    void setUp() {
        registrationDto = UserRegistrationDto.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Test@123")
                .phone("9876543210")
                .userType(UserType.CUSTOMER)
                .build();
        
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setPhone("9876543210");
        user.setUserType(UserType.CUSTOMER);
        user.setReferenceId(UUID.randomUUID());
        
        userResponseDto = UserResponseDto.builder()
                .referenceId(user.getReferenceId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .build();
    }
    
    @Test
    public void registerUser_Success() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDto(any(User.class))).thenReturn(userResponseDto);
        
        // When
        UserResponseDto result = userService.registerUser(registrationDto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Test@123");
    }
    
    @Test
    public void registerUser_UsernameExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.registerUser(registrationDto))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Username already exists");
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void registerUser_EmailExists_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.registerUser(registrationDto))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Email already exists");
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    public void login_Success() {
        // Given
        UserLoginDto loginDto = UserLoginDto.builder()
                .usernameOrEmail("testuser")
                .password("Test@123")
                .build();
        
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(86400000L);
        when(userMapper.toResponseDto(any(User.class))).thenReturn(userResponseDto);
        
        // When
        LoginResponseDto result = userService.login(loginDto);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getUser()).isEqualTo(userResponseDto);
        
        verify(jwtService).generateToken(user);
    }
    
    @Test
    public void login_InvalidCredentials_ThrowsException() {
        // Given
        UserLoginDto loginDto = UserLoginDto.builder()
                .usernameOrEmail("testuser")
                .password("wrongpassword")
                .build();
        
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid credentials");
        
        verify(jwtService, never()).generateToken(any(User.class));
    }
    
    @Test
    public void login_WrongPassword_ThrowsException() {
        // Given
        UserLoginDto loginDto = UserLoginDto.builder()
                .usernameOrEmail("testuser")
                .password("wrongpassword")
                .build();
        
        when(userRepository.findByUsernameOrEmail(anyString(), anyString()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> userService.login(loginDto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid credentials");
        
        verify(jwtService, never()).generateToken(any(User.class));
    }
    
    @Test
    public void getUserByReferenceId_Success() {
        // Given
        UUID referenceId = UUID.randomUUID();
        when(userRepository.findByReferenceId(referenceId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);
        
        // When
        UserResponseDto result = userService.getUserByReferenceId(referenceId);
        
        // Then
        assertThat(result).isEqualTo(userResponseDto);
        verify(userRepository).findByReferenceId(referenceId);
    }
    
    @Test
    public void existsByUsername_ReturnsTrue() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // When
        boolean result = userService.existsByUsername("testuser");
        
        // Then
        assertThat(result).isTrue();
        verify(userRepository).existsByUsername("testuser");
    }
    
    @Test
    public void existsByEmail_ReturnsFalse() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        
        // When
        boolean result = userService.existsByEmail("test@example.com");
        
        // Then
        assertThat(result).isFalse();
        verify(userRepository).existsByEmail("test@example.com");
    }
}
