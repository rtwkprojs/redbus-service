package com.redbus.journey.service.impl;

import com.redbus.common.exception.BusinessException;
import com.redbus.common.exception.ResourceNotFoundException;
import com.redbus.journey.dto.SeatInventoryDto;
import com.redbus.journey.entity.Journey;
import com.redbus.journey.entity.SeatInventory;
import com.redbus.journey.repository.JourneyRepository;
import com.redbus.journey.repository.SeatInventoryRepository;
import com.redbus.journey.service.SeatManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeatManagementServiceImpl implements SeatManagementService {
    
    private final SeatInventoryRepository seatInventoryRepository;
    private final JourneyRepository journeyRepository;
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<SeatInventoryDto> lockSeatsForBooking(UUID journeyReferenceId, List<UUID> seatInventoryIds) {
        log.info("Locking {} seats for journey: {}", seatInventoryIds.size(), journeyReferenceId);
        
        // Verify journey exists and is active
        Journey journey = journeyRepository.findByReferenceId(journeyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        if (!journey.getIsActive()) {
            throw new BusinessException("Journey is not active for booking");
        }
        
        // Lock seats with pessimistic locking
        List<SeatInventory> seats = seatInventoryRepository.findByReferenceIdsWithLock(seatInventoryIds);
        
        if (seats.size() != seatInventoryIds.size()) {
            throw new BusinessException("Some seats not found");
        }
        
        // Check if all seats are available
        List<SeatInventory> unavailableSeats = seats.stream()
                .filter(seat -> !seat.getIsAvailable())
                .collect(Collectors.toList());
        
        if (!unavailableSeats.isEmpty()) {
            String unavailableSeatNumbers = unavailableSeats.stream()
                    .map(SeatInventory::getSeatNumber)
                    .collect(Collectors.joining(", "));
            throw new BusinessException("Seats not available: " + unavailableSeatNumbers);
        }
        
        // Mark seats as unavailable (locked)
        seats.forEach(seat -> {
            seat.setIsAvailable(false);
            seat.setBookingReferenceId("LOCKED"); // Temporary lock indicator
        });
        
        seatInventoryRepository.saveAll(seats);
        
        // Update journey available seats count
        int bookedSeats = seats.size();
        journey.setAvailableSeats(journey.getAvailableSeats() - bookedSeats);
        journeyRepository.save(journey);
        
        log.info("Successfully locked {} seats for journey {}", seats.size(), journeyReferenceId);
        
        return seats.stream()
                .map(seat -> toSeatInventoryDto(seat, journey.getBaseFare()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateSeatBookingStatus(UUID journeyReferenceId, List<UUID> seatInventoryIds, String bookingReferenceId) {
        log.info("Updating seat booking status for journey: {} with booking: {}", journeyReferenceId, bookingReferenceId);
        
        // Verify journey exists
        Journey journey = journeyRepository.findByReferenceId(journeyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        // Update seats with actual booking reference
        seatInventoryRepository.updateSeatAvailability(seatInventoryIds, false, bookingReferenceId);
        
        log.info("Updated {} seats with booking reference: {}", seatInventoryIds.size(), bookingReferenceId);
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void releaseSeats(UUID journeyReferenceId, List<UUID> seatInventoryIds) {
        log.info("Releasing {} seats for journey: {}", seatInventoryIds.size(), journeyReferenceId);
        
        // Verify journey exists
        Journey journey = journeyRepository.findByReferenceId(journeyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        // Lock and release seats
        List<SeatInventory> seats = seatInventoryRepository.findByReferenceIdsWithLock(seatInventoryIds);
        
        seats.forEach(seat -> {
            seat.setIsAvailable(true);
            seat.setBookingReferenceId(null);
        });
        
        seatInventoryRepository.saveAll(seats);
        
        // Update journey available seats count
        int releasedSeats = seats.size();
        journey.setAvailableSeats(journey.getAvailableSeats() + releasedSeats);
        journeyRepository.save(journey);
        
        log.info("Successfully released {} seats for journey {}", seats.size(), journeyReferenceId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SeatInventoryDto> getSeatInventory(UUID journeyReferenceId, List<UUID> seatInventoryIds) {
        log.info("Getting seat inventory for journey: {}", journeyReferenceId);
        
        Journey journey = journeyRepository.findByReferenceId(journeyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        List<SeatInventory> seats;
        if (seatInventoryIds != null && !seatInventoryIds.isEmpty()) {
            seats = seatInventoryRepository.findAllById(
                    seatInventoryIds.stream()
                            .map(id -> seatInventoryRepository.findByReferenceId(id)
                                    .map(SeatInventory::getId)
                                    .orElse(null))
                            .filter(id -> id != null)
                            .collect(Collectors.toList())
            );
        } else {
            seats = seatInventoryRepository.findByJourneyId(journey.getId());
        }
        
        return seats.stream()
                .map(seat -> toSeatInventoryDto(seat, journey.getBaseFare()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkSeatAvailability(UUID journeyReferenceId, List<UUID> seatInventoryIds) {
        log.info("Checking seat availability for journey: {}", journeyReferenceId);
        
        Journey journey = journeyRepository.findByReferenceId(journeyReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Journey not found"));
        
        if (!journey.getIsActive()) {
            return false;
        }
        
        List<SeatInventory> seats = seatInventoryIds.stream()
                .map(id -> seatInventoryRepository.findByReferenceId(id).orElse(null))
                .filter(seat -> seat != null)
                .collect(Collectors.toList());
        
        return seats.size() == seatInventoryIds.size() && 
               seats.stream().allMatch(SeatInventory::getIsAvailable);
    }
    
    private SeatInventoryDto toSeatInventoryDto(SeatInventory seat, Double baseFare) {
        SeatInventoryDto dto = new SeatInventoryDto();
        dto.setReferenceId(seat.getReferenceId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatType(seat.getSeatType().name());
        dto.setIsAvailable(seat.getIsAvailable());
        dto.setIsLadiesSeat(seat.getIsLadiesSeat());
        dto.setFareMultiplier(seat.getFareMultiplier());
        dto.setCalculatedFare(baseFare * seat.getFareMultiplier());
        dto.setBookingReferenceId(seat.getBookingReferenceId());
        return dto;
    }
}
