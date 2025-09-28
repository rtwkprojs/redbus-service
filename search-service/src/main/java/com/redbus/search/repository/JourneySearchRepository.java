package com.redbus.search.repository;

import com.redbus.search.document.JourneyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JourneySearchRepository extends ElasticsearchRepository<JourneyDocument, String> {
    
    Page<JourneyDocument> findBySourceCityAndDestinationCityAndDepartureTimeBetween(
            String sourceCity, 
            String destinationCity, 
            LocalDateTime startTime, 
            LocalDateTime endTime,
            Pageable pageable);
    
    Page<JourneyDocument> findBySourceCityIgnoreCaseAndDestinationCityIgnoreCase(
            String sourceCity, 
            String destinationCity, 
            Pageable pageable);
    
    List<JourneyDocument> findByAvailableSeatsGreaterThanAndIsActiveTrue(Integer seats);
    
    @Query("{\"bool\": {\"must\": [" +
           "{\"match\": {\"sourceCity\": \"?0\"}}," +
           "{\"match\": {\"destinationCity\": \"?1\"}}," +
           "{\"range\": {\"departureTime\": {\"gte\": \"?2\", \"lte\": \"?3\"}}}," +
           "{\"range\": {\"availableSeats\": {\"gte\": ?4}}}," +
           "{\"term\": {\"isActive\": true}}" +
           "]}}")
    Page<JourneyDocument> searchJourneys(
            String sourceCity,
            String destinationCity,
            String startTime,
            String endTime,
            Integer minSeats,
            Pageable pageable);
    
    @Query("{\"bool\": {\"must\": [" +
           "{\"match\": {\"sourceCity\": \"?0\"}}," +
           "{\"match\": {\"destinationCity\": \"?1\"}}," +
           "{\"range\": {\"baseFare\": {\"lte\": ?2}}}," +
           "{\"term\": {\"isActive\": true}}" +
           "]}}")
    Page<JourneyDocument> findBySourceAndDestinationWithMaxFare(
            String sourceCity,
            String destinationCity,
            Double maxFare,
            Pageable pageable);
}
