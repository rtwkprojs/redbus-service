package com.redbus.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redbus.user.dto.UserLoginDto;
import com.redbus.user.dto.UserRegistrationDto;
import com.redbus.user.enums.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class UserControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void registerUser_Success() throws Exception {
        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
                .username("integrationtest")
                .email("integration@test.com")
                .password("Test@123")
                .phone("9876543210")
                .userType(UserType.CUSTOMER)
                .build();
        
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("integrationtest"))
                .andExpect(jsonPath("$.data.email").value("integration@test.com"));
    }
    
    @Test
    void registerAndLogin_Success() throws Exception {
        // Register user
        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
                .username("logintest")
                .email("login@test.com")
                .password("Test@123")
                .phone("9876543210")
                .userType(UserType.CUSTOMER)
                .build();
        
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());
        
        // Login with username
        UserLoginDto loginDto = UserLoginDto.builder()
                .usernameOrEmail("logintest")
                .password("Test@123")
                .build();
        
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.user.username").value("logintest"));
    }
    
    @Test
    void checkUsername_Exists() throws Exception {
        // Register user
        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
                .username("checkuser")
                .email("check@test.com")
                .password("Test@123")
                .phone("9876543210")
                .userType(UserType.CUSTOMER)
                .build();
        
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());
        
        // Check if username exists
        mockMvc.perform(get("/api/v1/users/check-username/checkuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
        
        // Check if non-existent username
        mockMvc.perform(get("/api/v1/users/check-username/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }
    
    @Test
    void registerUser_InvalidData_ReturnsBadRequest() throws Exception {
        UserRegistrationDto registrationDto = UserRegistrationDto.builder()
                .username("ab") // Too short
                .email("invalid-email") // Invalid email
                .password("weak") // Weak password
                .phone("123") // Invalid phone
                .userType(UserType.CUSTOMER)
                .build();
        
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }
}
