package com.redbus.search.controller;

import com.redbus.common.dto.ApiResponse;
import com.redbus.search.document.JourneyDocument;
import com.redbus.search.dto.SearchRequestDto;
import com.redbus.search.dto.SearchResponseDto;
import com.redbus.search.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class SearchController {
    
    private final SearchService searchService;
    
    @PostMapping("/journeys")
    public ResponseEntity<ApiResponse<SearchResponseDto>> searchJourneys(
            @Valid @RequestBody SearchRequestDto searchRequest) {
        log.info("Searching journeys: {} to {} on {}", 
                searchRequest.getSourceCity(), 
                searchRequest.getDestinationCity(), 
                searchRequest.getTravelDate());
        
        SearchResponseDto response = searchService.searchJourneys(searchRequest);
        return ResponseEntity.ok(ApiResponse.success(response, 
                String.format("Found %d journeys", response.getTotalResults())));
    }
    
    @GetMapping("/popular-routes")
    public ResponseEntity<ApiResponse<List<JourneyDocument>>> getPopularRoutes() {
        log.info("Fetching popular routes");
        List<JourneyDocument> routes = searchService.getPopularRoutes();
        return ResponseEntity.ok(ApiResponse.success(routes));
    }
    
    @PostMapping("/index")
    public ResponseEntity<ApiResponse<Void>> indexJourney(
            @RequestBody JourneyDocument journey) {
        log.info("Indexing journey: {}", journey.getJourneyCode());
        searchService.indexJourney(journey);
        return ResponseEntity.ok(ApiResponse.success(null, "Journey indexed successfully"));
    }
    
    @PutMapping("/journeys/{journeyId}/availability")
    public ResponseEntity<ApiResponse<Void>> updateAvailability(
            @PathVariable String journeyId,
            @RequestParam Integer availableSeats) {
        log.info("Updating availability for journey {}: {} seats", journeyId, availableSeats);
        searchService.updateJourneyAvailability(journeyId, availableSeats);
        return ResponseEntity.ok(ApiResponse.success(null, "Availability updated"));
    }
    
    @PostMapping("/reindex")
    public ResponseEntity<ApiResponse<Void>> reindexAll() {
        log.info("Triggering reindex of all journeys");
        searchService.reindexAllJourneys();
        return ResponseEntity.ok(ApiResponse.success(null, "Reindexing initiated"));
    }
}
