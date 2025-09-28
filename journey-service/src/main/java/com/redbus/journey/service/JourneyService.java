package com.redbus.journey.service;

import com.redbus.journey.dto.JourneyRequestDto;
import com.redbus.journey.dto.JourneyResponseDto;
import com.redbus.journey.dto.SeatInventoryDto;
import com.redbus.journey.enums.JourneyStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface JourneyService {
    
    JourneyResponseDto createJourney(JourneyRequestDto requestDto);
    
    JourneyResponseDto updateJourney(UUID referenceId, JourneyRequestDto requestDto);
    
    JourneyResponseDto getJourneyById(UUID referenceId);
    
    JourneyResponseDto getJourneyByCode(String journeyCode);
    
    List<JourneyResponseDto> getJourneysByRoute(UUID routeReferenceId);
    
    List<JourneyResponseDto> searchJourneys(String sourceCity, String destinationCity, LocalDate travelDate);
    
    List<JourneyResponseDto> getUpcomingJourneys();
    
    List<SeatInventoryDto> getJourneySeatInventory(UUID journeyReferenceId);
    
    void updateJourneyStatus(UUID referenceId, JourneyStatus status);
    
    void cancelJourney(UUID referenceId);
    
    void activateJourney(UUID referenceId);
    
    void deactivateJourney(UUID referenceId);
    
    List<JourneyResponseDto> getVehicleSchedule(String vehicleReferenceId, LocalDate date);
    
    JourneyResponseDto generateRecurringJourneys(UUID routeReferenceId, String vehicleReferenceId, 
                                                  LocalDate startDate, LocalDate endDate, 
                                                  String departureTime, List<Integer> daysOfWeek);
}
