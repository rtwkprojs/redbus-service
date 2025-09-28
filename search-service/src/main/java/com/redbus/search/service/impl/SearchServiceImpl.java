package com.redbus.search.service.impl;

import com.redbus.search.document.JourneyDocument;
import com.redbus.search.dto.*;
import com.redbus.search.repository.JourneySearchRepository;
import com.redbus.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
    
    private final JourneySearchRepository journeySearchRepository;
    
    @Override
    public SearchResponseDto searchJourneys(SearchRequestDto searchRequest) {
        log.info("Searching journeys from {} to {} on {}", 
                searchRequest.getSourceCity(), 
                searchRequest.getDestinationCity(), 
                searchRequest.getTravelDate());
        
        // Create date range for the travel date
        LocalDateTime startOfDay = searchRequest.getTravelDate().atStartOfDay();
        LocalDateTime endOfDay = searchRequest.getTravelDate().atTime(LocalTime.MAX);
        
        // Create pageable with sorting
        Sort sort = Sort.by(
                searchRequest.getSortOrder().equals("DESC") ? 
                Sort.Direction.DESC : Sort.Direction.ASC,
                searchRequest.getSortBy()
        );
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        
        // Search journeys
        Page<JourneyDocument> journeyPage = journeySearchRepository
                .findBySourceCityAndDestinationCityAndDepartureTimeBetween(
                        searchRequest.getSourceCity(),
                        searchRequest.getDestinationCity(),
                        startOfDay,
                        endOfDay,
                        pageable
                );
        
        // Apply additional filters if needed
        List<JourneySearchResultDto> filteredJourneys = journeyPage.getContent().stream()
                .filter(journey -> filterJourney(journey, searchRequest))
                .map(this::toSearchResultDto)
                .collect(Collectors.toList());
        
        // Build response
        return SearchResponseDto.builder()
                .journeys(filteredJourneys)
                .totalResults((int) journeyPage.getTotalElements())
                .totalPages(journeyPage.getTotalPages())
                .currentPage(searchRequest.getPage())
                .pageSize(searchRequest.getSize())
                .appliedFilters(buildAppliedFilters(searchRequest))
                .build();
    }
    
    @Override
    public List<JourneyDocument> getPopularRoutes() {
        log.info("Fetching popular routes");
        
        // For now, return journeys with most available seats
        return journeySearchRepository.findByAvailableSeatsGreaterThanAndIsActiveTrue(20);
    }
    
    @Override
    public void indexJourney(JourneyDocument journey) {
        log.info("Indexing journey: {}", journey.getJourneyCode());
        journeySearchRepository.save(journey);
    }
    
    @Override
    public void updateJourneyAvailability(String journeyId, Integer availableSeats) {
        log.info("Updating availability for journey {}: {} seats", journeyId, availableSeats);
        
        journeySearchRepository.findById(journeyId).ifPresent(journey -> {
            journey.setAvailableSeats(availableSeats);
            journey.setUpdatedAt(LocalDateTime.now());
            journeySearchRepository.save(journey);
        });
    }
    
    @Override
    public void deleteJourney(String journeyId) {
        log.info("Deleting journey from index: {}", journeyId);
        journeySearchRepository.deleteById(journeyId);
    }
    
    @Override
    public void reindexAllJourneys() {
        log.info("Reindexing all journeys");
        // This would typically fetch from Journey Service and reindex
        // For now, just log
        log.info("Reindexing would be implemented with Journey Service integration");
    }
    
    private boolean filterJourney(JourneyDocument journey, SearchRequestDto request) {
        // Check available seats
        if (journey.getAvailableSeats() < request.getSeatsRequired()) {
            return false;
        }
        
        // Check max fare
        if (request.getMaxFare() != null && journey.getBaseFare() > request.getMaxFare()) {
            return false;
        }
        
        // Check vehicle type
        if (request.getVehicleType() != null && 
            !journey.getVehicleType().equalsIgnoreCase(request.getVehicleType())) {
            return false;
        }
        
        // Check agency
        if (request.getAgencyName() != null && 
            !journey.getAgencyName().toLowerCase().contains(request.getAgencyName().toLowerCase())) {
            return false;
        }
        
        // Check AC
        if (request.getAcOnly() != null && request.getAcOnly()) {
            if (journey.getAmenities() == null || !journey.getAmenities().contains("AC")) {
                return false;
            }
        }
        
        // Check sleeper
        if (request.getSleeperOnly() != null && request.getSleeperOnly()) {
            if (journey.getVehicleType() == null || 
                !journey.getVehicleType().toLowerCase().contains("sleeper")) {
                return false;
            }
        }
        
        return journey.getIsActive();
    }
    
    private JourneySearchResultDto toSearchResultDto(JourneyDocument journey) {
        return JourneySearchResultDto.builder()
                .journeyId(journey.getId())
                .journeyCode(journey.getJourneyCode())
                .sourceCity(journey.getSourceCity())
                .destinationCity(journey.getDestinationCity())
                .departureTime(journey.getDepartureTime())
                .arrivalTime(journey.getArrivalTime())
                .durationMinutes(journey.getDurationMinutes())
                .distanceKm(journey.getDistanceKm())
                .baseFare(journey.getBaseFare())
                .availableSeats(journey.getAvailableSeats())
                .totalSeats(journey.getTotalSeats())
                .agencyName(journey.getAgencyName())
                .vehicleType(journey.getVehicleType())
                .routeName(journey.getRouteName())
                .amenities(journey.getAmenities())
                .isActive(journey.getIsActive())
                .status(journey.getStatus())
                .build();
    }
    
    private SearchFiltersDto buildAppliedFilters(SearchRequestDto request) {
        return SearchFiltersDto.builder()
                .sourceCity(request.getSourceCity())
                .destinationCity(request.getDestinationCity())
                .travelDate(request.getTravelDate())
                .seatsRequired(request.getSeatsRequired())
                .maxFare(request.getMaxFare())
                .vehicleType(request.getVehicleType())
                .agencyName(request.getAgencyName())
                .acOnly(request.getAcOnly())
                .sleeperOnly(request.getSleeperOnly())
                .build();
    }
}
