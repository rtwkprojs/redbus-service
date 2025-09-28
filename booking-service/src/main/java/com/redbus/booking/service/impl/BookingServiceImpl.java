package com.redbus.booking.service.impl;

import com.redbus.booking.client.JourneyServiceClient;
import com.redbus.booking.dto.*;
import com.redbus.booking.entity.Booking;
import com.redbus.booking.entity.BookingSeat;
import com.redbus.booking.entity.Passenger;
import com.redbus.booking.enums.BookingStatus;
import com.redbus.booking.repository.BookingRepository;
import com.redbus.booking.service.BookingService;
import com.redbus.common.exception.BusinessException;
import com.redbus.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final JourneyServiceClient journeyServiceClient;
    
    private static final int BOOKING_EXPIRY_MINUTES = 15;
    private static final String BOOKING_CODE_PREFIX = "BKG";
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponseDto initiateBooking(BookingRequestDto requestDto) {
        log.info("Initiating booking for journey: {} with {} seats", 
                requestDto.getJourneyReferenceId(), requestDto.getSeatSelections().size());
        
        // Validate seat count matches passenger count
        if (requestDto.getSeatSelections().size() != requestDto.getPassengers().size()) {
            throw new BusinessException("Number of seats must match number of passengers");
        }
        
        // Get journey details from Journey Service
        JourneyDetailsDto journey = journeyServiceClient.getJourneyDetails(requestDto.getJourneyReferenceId());
        
        // Check if journey is active and has available seats
        if (!journey.getIsActive()) {
            throw new BusinessException("Journey is not active for booking");
        }
        
        // Lock and check seat availability using pessimistic locking
        List<SeatInventoryDto> seats = journeyServiceClient.lockSeatsForBooking(
                requestDto.getJourneyReferenceId(),
                requestDto.getSeatSelections().stream()
                        .map(SeatSelectionDto::getSeatInventoryReferenceId)
                        .collect(Collectors.toList())
        );
        
        if (seats.isEmpty() || seats.stream().anyMatch(s -> !s.getIsAvailable())) {
            throw new BusinessException("Selected seats are not available");
        }
        
        // Create booking
        Booking booking = new Booking();
        booking.setBookingCode(generateBookingCode());
        booking.setUserReferenceId(requestDto.getUserReferenceId());
        booking.setJourneyReferenceId(requestDto.getJourneyReferenceId().toString());
        booking.setBoardingPointReferenceId(
                requestDto.getBoardingPointReferenceId() != null ? 
                requestDto.getBoardingPointReferenceId().toString() : null);
        booking.setDroppingPointReferenceId(
                requestDto.getDroppingPointReferenceId() != null ? 
                requestDto.getDroppingPointReferenceId().toString() : null);
        booking.setTotalSeats(requestDto.getSeatSelections().size());
        
        // Calculate total amount
        double totalAmount = seats.stream()
                .mapToDouble(SeatInventoryDto::getCalculatedFare)
                .sum();
        booking.setTotalAmount(totalAmount);
        booking.setDiscountAmount(0.0); // Apply promo code logic here if needed
        booking.setFinalAmount(totalAmount);
        
        booking.setBookingStatus(BookingStatus.SEATS_BLOCKED);
        booking.setPaymentStatus("PENDING");
        booking.setBookingTime(LocalDateTime.now());
        booking.setExpiryTime(LocalDateTime.now().plusMinutes(BOOKING_EXPIRY_MINUTES));
        booking.setContactEmail(requestDto.getContactEmail());
        booking.setContactPhone(requestDto.getContactPhone());
        
        booking = bookingRepository.save(booking);
        
        // Create passengers
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < requestDto.getPassengers().size(); i++) {
            PassengerDto passengerDto = requestDto.getPassengers().get(i);
            SeatSelectionDto seatSelection = requestDto.getSeatSelections().get(i);
            
            Passenger passenger = new Passenger();
            passenger.setBooking(booking);
            passenger.setSeatNumber(seatSelection.getSeatNumber());
            passenger.setPassengerName(passengerDto.getPassengerName());
            passenger.setAge(passengerDto.getAge());
            passenger.setGender(passengerDto.getGender());
            passenger.setIdType(passengerDto.getIdType());
            passenger.setIdNumber(passengerDto.getIdNumber());
            passenger.setIsPrimary(passengerDto.getIsPrimary());
            
            passengers.add(passenger);
        }
        booking.getPassengers().addAll(passengers);
        
        // Create booking seats
        List<BookingSeat> bookingSeats = new ArrayList<>();
        for (int i = 0; i < requestDto.getSeatSelections().size(); i++) {
            SeatSelectionDto seatSelection = requestDto.getSeatSelections().get(i);
            SeatInventoryDto seatInventory = seats.get(i);
            Passenger passenger = passengers.get(i);
            
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setBooking(booking);
            bookingSeat.setSeatInventoryReferenceId(seatSelection.getSeatInventoryReferenceId().toString());
            bookingSeat.setSeatNumber(seatSelection.getSeatNumber());
            bookingSeat.setPassengerReferenceId(passenger.getReferenceId().toString());
            bookingSeat.setSeatFare(seatInventory.getCalculatedFare());
            bookingSeat.setIsLocked(true);
            
            bookingSeats.add(bookingSeat);
        }
        booking.getBookingSeats().addAll(bookingSeats);
        
        booking = bookingRepository.save(booking);
        
        // Update seat inventory in Journey Service
        journeyServiceClient.updateSeatBookingStatus(
                requestDto.getJourneyReferenceId(),
                requestDto.getSeatSelections().stream()
                        .map(SeatSelectionDto::getSeatInventoryReferenceId)
                        .collect(Collectors.toList()),
                booking.getReferenceId().toString()
        );
        
        log.info("Booking initiated successfully with code: {}", booking.getBookingCode());
        return toBookingResponseDto(booking, journey);
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BookingResponseDto confirmBooking(UUID bookingReferenceId, PaymentCallbackDto paymentCallback) {
        log.info("Confirming booking: {} with payment: {}", bookingReferenceId, paymentCallback.getPaymentReferenceId());
        
        Booking booking = bookingRepository.findByReferenceIdWithLock(bookingReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        if (booking.getBookingStatus() != BookingStatus.SEATS_BLOCKED &&
            booking.getBookingStatus() != BookingStatus.PAYMENT_PENDING) {
            throw new BusinessException("Booking cannot be confirmed in current status: " + booking.getBookingStatus());
        }
        
        // Check if booking has expired
        if (LocalDateTime.now().isAfter(booking.getExpiryTime())) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            releaseBlockedSeats(bookingReferenceId);
            throw new BusinessException("Booking has expired");
        }
        
        // Update booking with payment details
        booking.setPaymentReferenceId(paymentCallback.getPaymentReferenceId());
        booking.setPaymentStatus(paymentCallback.getPaymentStatus());
        
        if ("SUCCESS".equals(paymentCallback.getPaymentStatus())) {
            booking.setBookingStatus(BookingStatus.CONFIRMED);
            log.info("Booking confirmed successfully: {}", booking.getBookingCode());
        } else {
            booking.setBookingStatus(BookingStatus.FAILED);
            releaseBlockedSeats(bookingReferenceId);
            log.warn("Booking failed due to payment failure: {}", booking.getBookingCode());
        }
        
        booking = bookingRepository.save(booking);
        
        JourneyDetailsDto journey = journeyServiceClient.getJourneyDetails(
                UUID.fromString(booking.getJourneyReferenceId()));
        
        return toBookingResponseDto(booking, journey);
    }
    
    @Override
    public BookingResponseDto cancelBooking(UUID bookingReferenceId, String reason) {
        log.info("Cancelling booking: {} with reason: {}", bookingReferenceId, reason);
        
        Booking booking = bookingRepository.findByReferenceIdWithLock(bookingReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Booking is already cancelled");
        }
        
        if (booking.getBookingStatus() != BookingStatus.CONFIRMED &&
            booking.getBookingStatus() != BookingStatus.SEATS_BLOCKED) {
            throw new BusinessException("Booking cannot be cancelled in current status: " + booking.getBookingStatus());
        }
        
        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.setCancellationTime(LocalDateTime.now());
        booking.setCancellationReason(reason);
        
        // Calculate refund amount based on cancellation policy
        if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
            booking.setRefundAmount(calculateRefundAmount(booking));
        }
        
        booking = bookingRepository.save(booking);
        
        // Release seats
        releaseBlockedSeats(bookingReferenceId);
        
        JourneyDetailsDto journey = journeyServiceClient.getJourneyDetails(
                UUID.fromString(booking.getJourneyReferenceId()));
        
        return toBookingResponseDto(booking, journey);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(UUID referenceId) {
        log.info("Fetching booking: {}", referenceId);
        
        Booking booking = bookingRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        JourneyDetailsDto journey = journeyServiceClient.getJourneyDetails(
                UUID.fromString(booking.getJourneyReferenceId()));
        
        return toBookingResponseDto(booking, journey);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingByCode(String bookingCode) {
        log.info("Fetching booking by code: {}", bookingCode);
        
        Booking booking = bookingRepository.findByBookingCode(bookingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        JourneyDetailsDto journey = journeyServiceClient.getJourneyDetails(
                UUID.fromString(booking.getJourneyReferenceId()));
        
        return toBookingResponseDto(booking, journey);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getUserBookings(String userReferenceId) {
        log.info("Fetching bookings for user: {}", userReferenceId);
        
        return bookingRepository.findByUserReferenceId(userReferenceId).stream()
                .map(booking -> {
                    JourneyDetailsDto journey = journeyServiceClient.getJourneyDetails(
                            UUID.fromString(booking.getJourneyReferenceId()));
                    return toBookingResponseDto(booking, journey);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getJourneyBookings(String journeyReferenceId) {
        log.info("Fetching bookings for journey: {}", journeyReferenceId);
        
        JourneyDetailsDto journey = journeyServiceClient.getJourneyDetails(
                UUID.fromString(journeyReferenceId));
        
        return bookingRepository.findByJourneyReferenceId(journeyReferenceId).stream()
                .map(booking -> toBookingResponseDto(booking, journey))
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateBookingStatus(UUID bookingReferenceId, BookingStatus status) {
        log.info("Updating booking {} status to {}", bookingReferenceId, status);
        
        Booking booking = bookingRepository.findByReferenceIdWithLock(bookingReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        booking.setBookingStatus(status);
        bookingRepository.save(booking);
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void processExpiredBookings() {
        log.info("Processing expired bookings");
        
        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(
                BookingStatus.SEATS_BLOCKED, LocalDateTime.now());
        
        for (Booking booking : expiredBookings) {
            booking.setBookingStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            releaseBlockedSeats(booking.getReferenceId());
        }
        
        log.info("Processed {} expired bookings", expiredBookings.size());
    }
    
    @Override
    public void releaseBlockedSeats(UUID bookingReferenceId) {
        log.info("Releasing blocked seats for booking: {}", bookingReferenceId);
        
        Booking booking = bookingRepository.findByReferenceId(bookingReferenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        
        List<UUID> seatInventoryIds = booking.getBookingSeats().stream()
                .map(seat -> UUID.fromString(seat.getSeatInventoryReferenceId()))
                .collect(Collectors.toList());
        
        journeyServiceClient.releaseSeats(
                UUID.fromString(booking.getJourneyReferenceId()),
                seatInventoryIds
        );
        
        // Update booking seats as unlocked
        booking.getBookingSeats().forEach(seat -> seat.setIsLocked(false));
        bookingRepository.save(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkSeatAvailability(UUID journeyReferenceId, List<UUID> seatInventoryIds) {
        log.info("Checking seat availability for journey: {}", journeyReferenceId);
        
        List<SeatInventoryDto> seats = journeyServiceClient.getSeatInventory(
                journeyReferenceId, seatInventoryIds);
        
        return seats.stream().allMatch(SeatInventoryDto::getIsAvailable);
    }
    
    private String generateBookingCode() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return String.format("%s%s%s", BOOKING_CODE_PREFIX, datePart, uniquePart);
    }
    
    private Double calculateRefundAmount(Booking booking) {
        // Simple refund policy: 
        // - More than 24 hours before journey: 90% refund
        // - 6-24 hours before journey: 50% refund
        // - Less than 6 hours: No refund
        
        // This would need journey departure time from Journey Service
        // For now, returning 50% as default
        return booking.getFinalAmount() * 0.5;
    }
    
    private BookingResponseDto toBookingResponseDto(Booking booking, JourneyDetailsDto journey) {
        return BookingResponseDto.builder()
                .referenceId(booking.getReferenceId())
                .bookingCode(booking.getBookingCode())
                .userReferenceId(booking.getUserReferenceId())
                .journeyReferenceId(UUID.fromString(booking.getJourneyReferenceId()))
                .journeyCode(journey.getJourneyCode())
                .sourceCity(journey.getSourceCity())
                .destinationCity(journey.getDestinationCity())
                .departureTime(journey.getDepartureTime())
                .arrivalTime(journey.getArrivalTime())
                .totalSeats(booking.getTotalSeats())
                .totalAmount(booking.getTotalAmount())
                .discountAmount(booking.getDiscountAmount())
                .finalAmount(booking.getFinalAmount())
                .bookingStatus(booking.getBookingStatus())
                .paymentStatus(booking.getPaymentStatus())
                .paymentReferenceId(booking.getPaymentReferenceId())
                .bookingTime(booking.getBookingTime())
                .expiryTime(booking.getExpiryTime())
                .contactEmail(booking.getContactEmail())
                .contactPhone(booking.getContactPhone())
                .passengers(booking.getPassengers().stream()
                        .map(this::toPassengerResponseDto)
                        .collect(Collectors.toList()))
                .seats(booking.getBookingSeats().stream()
                        .map(this::toBookingSeatDto)
                        .collect(Collectors.toList()))
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
    
    private PassengerResponseDto toPassengerResponseDto(Passenger passenger) {
        return PassengerResponseDto.builder()
                .referenceId(passenger.getReferenceId())
                .seatNumber(passenger.getSeatNumber())
                .passengerName(passenger.getPassengerName())
                .age(passenger.getAge())
                .gender(passenger.getGender())
                .idType(passenger.getIdType())
                .idNumber(passenger.getIdNumber())
                .isPrimary(passenger.getIsPrimary())
                .build();
    }
    
    private BookingSeatDto toBookingSeatDto(BookingSeat seat) {
        return BookingSeatDto.builder()
                .referenceId(seat.getReferenceId())
                .seatInventoryReferenceId(UUID.fromString(seat.getSeatInventoryReferenceId()))
                .seatNumber(seat.getSeatNumber())
                .passengerReferenceId(UUID.fromString(seat.getPassengerReferenceId()))
                .seatFare(seat.getSeatFare())
                .isLocked(seat.getIsLocked())
                .build();
    }
}
