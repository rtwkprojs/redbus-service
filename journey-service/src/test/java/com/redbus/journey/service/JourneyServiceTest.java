package com.redbus.journey.service;

import com.redbus.journey.dto.JourneyRequestDto;
import com.redbus.journey.dto.JourneyResponseDto;
import com.redbus.journey.entity.Journey;
import com.redbus.journey.entity.Route;
import com.redbus.journey.enums.JourneyStatus;
// import com.redbus.journey.mapper.JourneyMapper; // Mapper not used
import com.redbus.journey.repository.JourneyRepository;
import com.redbus.journey.repository.RouteRepository;
import com.redbus.journey.repository.SeatInventoryRepository;
import com.redbus.journey.service.impl.JourneyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JourneyServiceTest {

    @Mock
    private JourneyRepository journeyRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private SeatInventoryRepository seatInventoryRepository;

    @InjectMocks
    private JourneyServiceImpl journeyService;

    private JourneyRequestDto requestDto;
    private Journey journey;
    private Route route;
    private JourneyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new JourneyRequestDto();
        requestDto.setRouteReferenceId(UUID.randomUUID());
        requestDto.setVehicleReferenceId(UUID.randomUUID().toString());
        requestDto.setDepartureTime(LocalDateTime.now().plusDays(1));
        requestDto.setBaseFare(800.0);

        route = new Route();
        route.setReferenceId(requestDto.getRouteReferenceId());
        route.setSourceCity("Bangalore");
        route.setDestinationCity("Chennai");
        route.setEstimatedDurationMinutes(360); // Add this to fix null pointer
        route.setDistanceKm(350);
        route.setBaseFare(800.0);
        route.setIsActive(true);

        journey = new Journey();
        journey.setReferenceId(UUID.randomUUID());
        journey.setJourneyCode("JRN1234TEST");
        journey.setRoute(route);
        journey.setDepartureTime(requestDto.getDepartureTime());
        journey.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(6));
        journey.setBaseFare(requestDto.getBaseFare());
        journey.setTotalSeats(40);
        journey.setAvailableSeats(40);
        journey.setJourneyStatus(JourneyStatus.SCHEDULED);

        responseDto = new JourneyResponseDto();
        responseDto.setReferenceId(journey.getReferenceId());
        responseDto.setJourneyCode(journey.getJourneyCode());
        responseDto.setSourceCity(route.getSourceCity());
        responseDto.setDestinationCity(route.getDestinationCity());
        responseDto.setDepartureTime(journey.getDepartureTime());
        responseDto.setArrivalTime(journey.getArrivalTime());
        responseDto.setBaseFare(journey.getBaseFare());
        responseDto.setAvailableSeats(journey.getAvailableSeats());
    }

    @Test
    void createJourney_Success() {
        when(routeRepository.findByReferenceId(any(UUID.class))).thenReturn(Optional.of(route));
        when(journeyRepository.save(any(Journey.class))).thenReturn(journey);
        // when(journeyMapper.toResponseDto(any(Journey.class))).thenReturn(responseDto);

        JourneyResponseDto result = journeyService.createJourney(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getJourneyCode()).isEqualTo("JRN1234TEST");
        assertThat(result.getSourceCity()).isEqualTo("Bangalore");
        assertThat(result.getDestinationCity()).isEqualTo("Chennai");
        verify(journeyRepository).save(any(Journey.class));
        verify(seatInventoryRepository).saveAll(any()); // Seats created via saveAll
    }

    @Test
    void getJourneyByReferenceId_Success() {
        UUID referenceId = UUID.randomUUID();
        when(journeyRepository.findByReferenceId(any(UUID.class))).thenReturn(Optional.of(journey));
        // when(journeyMapper.toResponseDto(any(Journey.class))).thenReturn(responseDto);

        JourneyResponseDto result = journeyService.getJourneyById(referenceId);

        assertThat(result).isNotNull();
        assertThat(result.getJourneyCode()).isEqualTo("JRN1234TEST");
    }

    @Test
    void updateJourneyStatus_Success() {
        UUID referenceId = UUID.randomUUID();
        when(journeyRepository.findByReferenceId(any(UUID.class))).thenReturn(Optional.of(journey));

        journeyService.updateJourneyStatus(referenceId, JourneyStatus.BOARDING);

        verify(journeyRepository).save(any(Journey.class));
    }

    @Test
    void cancelJourney_Success() {
        UUID referenceId = UUID.randomUUID();
        when(journeyRepository.findByReferenceId(any(UUID.class))).thenReturn(Optional.of(journey));

        journeyService.cancelJourney(referenceId);

        assertThat(journey.getJourneyStatus()).isEqualTo(JourneyStatus.CANCELLED);
        verify(journeyRepository).save(any(Journey.class));
    }
}
