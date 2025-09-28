package com.redbus.journey.service;

import com.redbus.journey.dto.RouteRequestDto;
import com.redbus.journey.dto.RouteResponseDto;

import java.util.List;
import java.util.UUID;

public interface RouteService {
    
    RouteResponseDto createRoute(RouteRequestDto requestDto);
    
    RouteResponseDto updateRoute(UUID referenceId, RouteRequestDto requestDto);
    
    RouteResponseDto getRouteById(UUID referenceId);
    
    RouteResponseDto getRouteWithStops(UUID referenceId);
    
    List<RouteResponseDto> getRoutesByAgency(String agencyReferenceId);
    
    List<RouteResponseDto> getRoutesByCities(String sourceCity, String destinationCity);
    
    List<RouteResponseDto> getAllActiveRoutes();
    
    void activateRoute(UUID referenceId);
    
    void deactivateRoute(UUID referenceId);
    
    void deleteRoute(UUID referenceId);
}
