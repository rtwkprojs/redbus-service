package com.redbus.booking.client;

import com.redbus.booking.dto.JourneyDetailsDto;
import com.redbus.booking.dto.SeatInventoryDto;
import com.redbus.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JourneyServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${journey.service.url:http://localhost:8083}")
    private String journeyServiceUrl;
    
    public JourneyDetailsDto getJourneyDetails(UUID journeyReferenceId) {
        log.info("Fetching journey details: {}", journeyReferenceId);
        
        String url = journeyServiceUrl + "/api/v1/journeys/" + journeyReferenceId;
        
        ResponseEntity<ApiResponse<JourneyDetailsDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<JourneyDetailsDto>>() {}
        );
        
        if (response.getBody() != null && response.getBody().isSuccess()) {
            return response.getBody().getData();
        }
        
        throw new RuntimeException("Failed to fetch journey details");
    }
    
    public List<SeatInventoryDto> lockSeatsForBooking(UUID journeyReferenceId, List<UUID> seatInventoryIds) {
        log.info("Locking seats for journey: {} with seats: {}", journeyReferenceId, seatInventoryIds);
        
        String url = journeyServiceUrl + "/api/v1/journeys/" + journeyReferenceId + "/seats/lock";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> request = Map.of("seatInventoryIds", seatInventoryIds);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<ApiResponse<List<SeatInventoryDto>>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<ApiResponse<List<SeatInventoryDto>>>() {}
        );
        
        if (response.getBody() != null && response.getBody().isSuccess()) {
            return response.getBody().getData();
        }
        
        throw new RuntimeException("Failed to lock seats");
    }
    
    public void updateSeatBookingStatus(UUID journeyReferenceId, List<UUID> seatInventoryIds, String bookingReferenceId) {
        log.info("Updating seat booking status for journey: {}", journeyReferenceId);
        
        String url = journeyServiceUrl + "/api/v1/journeys/" + journeyReferenceId + "/seats/update-booking";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> request = Map.of(
                "seatInventoryIds", seatInventoryIds,
                "bookingReferenceId", bookingReferenceId
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }
    
    public void releaseSeats(UUID journeyReferenceId, List<UUID> seatInventoryIds) {
        log.info("Releasing seats for journey: {}", journeyReferenceId);
        
        String url = journeyServiceUrl + "/api/v1/journeys/" + journeyReferenceId + "/seats/release";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> request = Map.of("seatInventoryIds", seatInventoryIds);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        
        restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
    }
    
    public List<SeatInventoryDto> getSeatInventory(UUID journeyReferenceId, List<UUID> seatInventoryIds) {
        log.info("Getting seat inventory for journey: {}", journeyReferenceId);
        
        String url = journeyServiceUrl + "/api/v1/journeys/" + journeyReferenceId + "/seats";
        
        ResponseEntity<ApiResponse<List<SeatInventoryDto>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ApiResponse<List<SeatInventoryDto>>>() {}
        );
        
        if (response.getBody() != null && response.getBody().isSuccess()) {
            List<SeatInventoryDto> allSeats = response.getBody().getData();
            return allSeats.stream()
                    .filter(seat -> seatInventoryIds.contains(seat.getReferenceId()))
                    .toList();
        }
        
        throw new RuntimeException("Failed to get seat inventory");
    }
}
