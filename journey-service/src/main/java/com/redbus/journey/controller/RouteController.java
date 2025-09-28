package com.redbus.journey.controller;

import com.redbus.common.dto.ApiResponse;
import com.redbus.journey.dto.RouteRequestDto;
import com.redbus.journey.dto.RouteResponseDto;
import com.redbus.journey.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class RouteController {
    
    private final RouteService routeService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<RouteResponseDto>> createRoute(
            @Valid @RequestBody RouteRequestDto requestDto) {
        log.info("Creating route: {}", requestDto.getRouteName());
        RouteResponseDto route = routeService.createRoute(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(route, "Route created successfully"));
    }
    
    @PutMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<RouteResponseDto>> updateRoute(
            @PathVariable UUID referenceId,
            @Valid @RequestBody RouteRequestDto requestDto) {
        log.info("Updating route: {}", referenceId);
        RouteResponseDto route = routeService.updateRoute(referenceId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(route, "Route updated successfully"));
    }
    
    @GetMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<RouteResponseDto>> getRoute(@PathVariable UUID referenceId) {
        log.info("Fetching route: {}", referenceId);
        RouteResponseDto route = routeService.getRouteById(referenceId);
        return ResponseEntity.ok(ApiResponse.success(route));
    }
    
    @GetMapping("/{referenceId}/with-stops")
    public ResponseEntity<ApiResponse<RouteResponseDto>> getRouteWithStops(@PathVariable UUID referenceId) {
        log.info("Fetching route with stops: {}", referenceId);
        RouteResponseDto route = routeService.getRouteWithStops(referenceId);
        return ResponseEntity.ok(ApiResponse.success(route));
    }
    
    @GetMapping("/agency/{agencyReferenceId}")
    public ResponseEntity<ApiResponse<List<RouteResponseDto>>> getRoutesByAgency(
            @PathVariable String agencyReferenceId) {
        log.info("Fetching routes for agency: {}", agencyReferenceId);
        List<RouteResponseDto> routes = routeService.getRoutesByAgency(agencyReferenceId);
        return ResponseEntity.ok(ApiResponse.success(routes));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<RouteResponseDto>>> searchRoutes(
            @RequestParam String sourceCity,
            @RequestParam String destinationCity) {
        log.info("Searching routes from {} to {}", sourceCity, destinationCity);
        List<RouteResponseDto> routes = routeService.getRoutesByCities(sourceCity, destinationCity);
        return ResponseEntity.ok(ApiResponse.success(routes));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<RouteResponseDto>>> getAllActiveRoutes() {
        log.info("Fetching all active routes");
        List<RouteResponseDto> routes = routeService.getAllActiveRoutes();
        return ResponseEntity.ok(ApiResponse.success(routes));
    }
    
    @PostMapping("/{referenceId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateRoute(@PathVariable UUID referenceId) {
        log.info("Activating route: {}", referenceId);
        routeService.activateRoute(referenceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Route activated successfully"));
    }
    
    @PostMapping("/{referenceId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateRoute(@PathVariable UUID referenceId) {
        log.info("Deactivating route: {}", referenceId);
        routeService.deactivateRoute(referenceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Route deactivated successfully"));
    }
    
    @DeleteMapping("/{referenceId}")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable UUID referenceId) {
        log.info("Deleting route: {}", referenceId);
        routeService.deleteRoute(referenceId);
        return ResponseEntity.ok(ApiResponse.success(null, "Route deleted successfully"));
    }
}
