package com.redbus.search.service;

import com.redbus.search.document.JourneyDocument;
import com.redbus.search.dto.SearchRequestDto;
import com.redbus.search.dto.SearchResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SearchService {
    
    /**
     * Search journeys based on criteria
     */
    SearchResponseDto searchJourneys(SearchRequestDto searchRequest);
    
    /**
     * Get popular routes
     */
    List<JourneyDocument> getPopularRoutes();
    
    /**
     * Index a journey document
     */
    void indexJourney(JourneyDocument journey);
    
    /**
     * Update journey availability
     */
    void updateJourneyAvailability(String journeyId, Integer availableSeats);
    
    /**
     * Delete journey from index
     */
    void deleteJourney(String journeyId);
    
    /**
     * Reindex all journeys (for maintenance)
     */
    void reindexAllJourneys();
}
