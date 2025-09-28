package com.redbus.journey.service.impl;

import com.redbus.common.exception.BusinessException;
import com.redbus.common.exception.ResourceNotFoundException;
import com.redbus.journey.dto.JourneyRequestDto;
import com.redbus.journey.dto.JourneyResponseDto;
import com.redbus.journey.dto.SeatConfigDto;
import com.redbus.journey.dto.SeatInventoryDto;
import com.redbus.journey.entity.*;
import com.redbus.journey.enums.JourneyStatus;
import com.redbus.journey.repository.JourneyRepository;
import com.redbus.journey.repository.RouteRepository;
import com.redbus.journey.repository.SeatInventoryRepository;
import com.redbus.journey.service.JourneyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JourneyServiceImpl implements JourneyService {
    
    private final JourneyRepository journeyRepository;
    private final RouteRepository routeRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    
    private static final int DEFAULT_TOTAL_SEATS = 40;
    private static final String JOURNEY_CODE_PREFIX = "JRN";
    
    @Override
    public JourneyResponseDto createJourney(JourneyRequestDto requestDto) {
        log.info("Creating journey for route: {} on {}", requestDto.getRouteReferenceId(), requestDto.getDepartureTime());
        
        Route route = routeRepository.findByReferenceId(requestDto.getRouteReferenceId())
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        // Generate unique journey code
        String journeyCode = generateJourneyCode(route, requestDto.getDepartureTime());
        
        // Check for vehicle conflicts
        LocalDateTime arrivalTime = requestDto.getDepartureTime().plusMinutes(route.getEstimatedDurationMinutes());
        List<Journey> conflicts = journeyRepository.findVehicleSchedule(
                requestDto.getVehicleReferenceId(),
                requestDto.getDepartureTime().minusHours(1),
                arrivalTime.plusHours(1)
        );
        
        if (!conflicts.isEmpty()) {
            throw new BusinessException("Vehicle has conflicting schedule");
        }
        
        Journey journey = new Journey();
        journey.setRoute(route);
        journey.setJourneyCode(journeyCode);
        journey.setVehicleReferenceId(requestDto.getVehicleReferenceId());
        journey.setDepartureTime(requestDto.getDepartureTime());
        journey.setArrivalTime(arrivalTime);
        journey.setJourneyStatus(JourneyStatus.SCHEDULED);
        journey.setTotalSeats(DEFAULT_TOTAL_SEATS);
        journey.setAvailableSeats(DEFAULT_TOTAL_SEATS);
        journey.setBaseFare(requestDto.getBaseFare() != null ? requestDto.getBaseFare() : route.getBaseFare());
        journey.setIsActive(true);
        journey.setAmenities(requestDto.getAmenities());
        
        journey = journeyRepository.save(journey);
        
        // Create seat inventory
        createSeatInventory(journey, requestDto.getSeatConfiguration());
        
        log.info("Journey created with code: {}", journeyCode);
        return toJourneyResponseDto(journey);
    }
    
    @Override
    public JourneyResponseDto updateJourney(UUID referenceId, JourneyRequestDto requestDto) {
        log.info("Updating journey: {}", referenceId);
        
        Journey journey = journeyRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        if (journey.getJourneyStatus() != JourneyStatus.SCHEDULED) {
            throw new BusinessException("Cannot update journey that is not in SCHEDULED status");
        }
        
        Route route = journey.getRoute();
        LocalDateTime arrivalTime = requestDto.getDepartureTime().plusMinutes(route.getEstimatedDurationMinutes());
        
        journey.setVehicleReferenceId(requestDto.getVehicleReferenceId());
        journey.setDepartureTime(requestDto.getDepartureTime());
        journey.setArrivalTime(arrivalTime);
        if (requestDto.getBaseFare() != null) {
            journey.setBaseFare(requestDto.getBaseFare());
        }
        journey.setAmenities(requestDto.getAmenities());
        
        journey = journeyRepository.save(journey);
        return toJourneyResponseDto(journey);
    }
    
    @Override
    @Transactional(readOnly = true)
    public JourneyResponseDto getJourneyById(UUID referenceId) {
        log.info("Fetching journey: {}", referenceId);
        
        Journey journey = journeyRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        return toJourneyResponseDto(journey);
    }
    
    @Override
    @Transactional(readOnly = true)
    public JourneyResponseDto getJourneyByCode(String journeyCode) {
        log.info("Fetching journey by code: {}", journeyCode);
        
        Journey journey = journeyRepository.findByJourneyCode(journeyCode)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        return toJourneyResponseDto(journey);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JourneyResponseDto> getJourneysByRoute(UUID routeReferenceId) {
        log.info("Fetching journeys for route: {}", routeReferenceId);
        
        Route route = routeRepository.findByReferenceId(routeReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        return journeyRepository.findByRouteId(route.getId()).stream()
                .map(this::toJourneyResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JourneyResponseDto> searchJourneys(String sourceCity, String destinationCity, LocalDate travelDate) {
        log.info("Searching journeys from {} to {} on {}", sourceCity, destinationCity, travelDate);
        
        LocalDateTime startOfDay = travelDate.atStartOfDay();
        
        return journeyRepository.findBySourceDestinationAndDate(sourceCity, destinationCity, startOfDay).stream()
                .filter(j -> j.getIsActive() && j.getAvailableSeats() > 0)
                .map(this::toJourneyResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JourneyResponseDto> getUpcomingJourneys() {
        log.info("Fetching upcoming journeys");
        
        return journeyRepository.findUpcomingJourneys(LocalDateTime.now()).stream()
                .limit(50)
                .map(this::toJourneyResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SeatInventoryDto> getJourneySeatInventory(UUID journeyReferenceId) {
        log.info("Fetching seat inventory for journey: {}", journeyReferenceId);
        
        Journey journey = journeyRepository.findByReferenceId(journeyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        return seatInventoryRepository.findByJourneyIdOrderBySeatNumber(journey.getId()).stream()
                .map(seat -> toSeatInventoryDto(seat, journey.getBaseFare()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateJourneyStatus(UUID referenceId, JourneyStatus status) {
        log.info("Updating journey {} status to {}", referenceId, status);
        
        Journey journey = journeyRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        journey.setJourneyStatus(status);
        journeyRepository.save(journey);
    }
    
    @Override
    public void cancelJourney(UUID referenceId) {
        log.info("Cancelling journey: {}", referenceId);
        
        Journey journey = journeyRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        if (journey.getJourneyStatus() == JourneyStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel completed journey");
        }
        
        journey.setJourneyStatus(JourneyStatus.CANCELLED);
        journey.setIsActive(false);
        journeyRepository.save(journey);
    }
    
    @Override
    public void activateJourney(UUID referenceId) {
        log.info("Activating journey: {}", referenceId);
        
        Journey journey = journeyRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        journey.setIsActive(true);
        journeyRepository.save(journey);
    }
    
    @Override
    public void deactivateJourney(UUID referenceId) {
        log.info("Deactivating journey: {}", referenceId);
        
        Journey journey = journeyRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        journey.setIsActive(false);
        journeyRepository.save(journey);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<JourneyResponseDto> getVehicleSchedule(String vehicleReferenceId, LocalDate date) {
        log.info("Fetching vehicle {} schedule for {}", vehicleReferenceId, date);
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        return journeyRepository.findVehicleSchedule(vehicleReferenceId, startOfDay, endOfDay).stream()
                .map(this::toJourneyResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public JourneyResponseDto generateRecurringJourneys(UUID routeReferenceId, String vehicleReferenceId,
                                                        LocalDate startDate, LocalDate endDate,
                                                        String departureTime, List<Integer> daysOfWeek) {
        log.info("Generating recurring journeys for route: {}", routeReferenceId);
        
        // This is a placeholder for recurring journey generation
        // In a real implementation, this would create multiple journeys based on the schedule
        throw new UnsupportedOperationException("Recurring journey generation not yet implemented");
    }
    
    private void createSeatInventory(Journey journey, List<SeatConfigDto> seatConfig) {
        List<SeatInventory> seats = new ArrayList<>();
        
        if (seatConfig != null && !seatConfig.isEmpty()) {
            // Use custom seat configuration
            for (SeatConfigDto config : seatConfig) {
                SeatInventory seat = new SeatInventory();
                seat.setJourney(journey);
                seat.setSeatNumber(config.getSeatNumber());
                seat.setSeatType(config.getSeatType());
                seat.setIsAvailable(true);
                seat.setIsLadiesSeat(config.getIsLadiesSeat());
                seat.setFareMultiplier(config.getFareMultiplier());
                seats.add(seat);
            }
        } else {
            // Generate default seat configuration
            for (int i = 1; i <= journey.getTotalSeats(); i++) {
                SeatInventory seat = new SeatInventory();
                seat.setJourney(journey);
                seat.setSeatNumber(String.format("S%02d", i));
                seat.setSeatType(SeatType.SEATER);
                seat.setIsAvailable(true);
                seat.setIsLadiesSeat(i % 10 == 1); // Every 10th seat is ladies seat
                seat.setFareMultiplier(1.0);
                seats.add(seat);
            }
        }
        
        seatInventoryRepository.saveAll(seats);
    }
    
    private String generateJourneyCode(Route route, LocalDateTime departureTime) {
        String datePart = departureTime.format(DateTimeFormatter.ofPattern("MMdd"));
        String routePart = route.getSourceCity().substring(0, 2).toUpperCase();
        String uniquePart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return String.format("%s%s%s%s", JOURNEY_CODE_PREFIX, datePart, routePart, uniquePart);
    }
    
    private JourneyResponseDto toJourneyResponseDto(Journey journey) {
        Route route = journey.getRoute();
        return JourneyResponseDto.builder()
                .referenceId(journey.getReferenceId())
                .journeyCode(journey.getJourneyCode())
                .routeReferenceId(route.getReferenceId())
                .routeName(route.getRouteName())
                .sourceCity(route.getSourceCity())
                .destinationCity(route.getDestinationCity())
                .vehicleReferenceId(journey.getVehicleReferenceId())
                .departureTime(journey.getDepartureTime())
                .arrivalTime(journey.getArrivalTime())
                .journeyStatus(journey.getJourneyStatus())
                .totalSeats(journey.getTotalSeats())
                .availableSeats(journey.getAvailableSeats())
                .baseFare(journey.getBaseFare())
                .isActive(journey.getIsActive())
                .amenities(journey.getAmenities())
                .estimatedDurationMinutes(route.getEstimatedDurationMinutes())
                .createdAt(journey.getCreatedAt())
                .updatedAt(journey.getUpdatedAt())
                .build();
    }
    
    private SeatInventoryDto toSeatInventoryDto(SeatInventory seat, Double baseFare) {
        return SeatInventoryDto.builder()
                .referenceId(seat.getReferenceId())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .isAvailable(seat.getIsAvailable())
                .isLadiesSeat(seat.getIsLadiesSeat())
                .fareMultiplier(seat.getFareMultiplier())
                .calculatedFare(baseFare * seat.getFareMultiplier())
                .bookingReferenceId(seat.getBookingReferenceId())
                .build();
    }
}
