package com.redbus.payment.client;

import com.redbus.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${booking.service.url:http://localhost:8085}")
    private String bookingServiceUrl;
    
    public void confirmBookingPayment(UUID bookingReferenceId, String paymentReferenceId, 
                                     String paymentStatus, Double amount) {
        log.info("Sending payment confirmation to booking service for booking: {}", bookingReferenceId);
        
        String url = bookingServiceUrl + "/api/v1/bookings/" + bookingReferenceId + "/confirm";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> request = new HashMap<>();
        request.put("paymentReferenceId", paymentReferenceId);
        request.put("paymentStatus", paymentStatus);
        request.put("paymentAmount", amount);
        request.put("paymentMethod", "MOCK");
        request.put("paymentTime", LocalDateTime.now().toString());
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<Object>>() {}
            );
            
            if (response.getBody() != null && response.getBody().isSuccess()) {
                log.info("Successfully confirmed booking payment for: {}", bookingReferenceId);
            } else {
                log.error("Failed to confirm booking payment: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error calling booking service for payment confirmation", e);
            // In production, this should be handled with retry logic or message queue
        }
    }
    
    public void cancelBookingPayment(UUID bookingReferenceId, String reason) {
        log.info("Sending payment cancellation to booking service for booking: {}", bookingReferenceId);
        
        String url = bookingServiceUrl + "/api/v1/bookings/" + bookingReferenceId + "/cancel";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> request = new HashMap<>();
        request.put("reason", reason);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<Object>>() {}
            );
            
            if (response.getBody() != null && response.getBody().isSuccess()) {
                log.info("Successfully cancelled booking for failed payment: {}", bookingReferenceId);
            }
        } catch (Exception e) {
            log.error("Error calling booking service for cancellation", e);
        }
    }
}
