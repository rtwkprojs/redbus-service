package com.redbus.journey.service.impl;

import com.redbus.common.exception.BusinessException;
import com.redbus.common.exception.ResourceNotFoundException;
import com.redbus.journey.dto.RouteRequestDto;
import com.redbus.journey.dto.RouteResponseDto;
import com.redbus.journey.dto.RouteStopRequestDto;
import com.redbus.journey.dto.RouteStopResponseDto;
import com.redbus.journey.entity.Route;
import com.redbus.journey.entity.RouteStop;
import com.redbus.journey.entity.Stop;
import com.redbus.journey.repository.RouteRepository;
import com.redbus.journey.repository.StopRepository;
import com.redbus.journey.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RouteServiceImpl implements RouteService {
    
    private final RouteRepository routeRepository;
    private final StopRepository stopRepository;
    
    @Override
    public RouteResponseDto createRoute(RouteRequestDto requestDto) {
        log.info("Creating route: {} for agency: {}", requestDto.getRouteName(), requestDto.getAgencyReferenceId());
        
        // Check if route with same name already exists for agency
        if (routeRepository.existsByRouteNameAndAgencyReferenceId(
                requestDto.getRouteName(), requestDto.getAgencyReferenceId())) {
            throw new BusinessException("Route with this name already exists for the agency");
        }
        
        Route route = new Route();
        route.setRouteName(requestDto.getRouteName());
        route.setSourceCity(requestDto.getSourceCity());
        route.setDestinationCity(requestDto.getDestinationCity());
        route.setDistanceKm(requestDto.getDistanceKm());
        route.setEstimatedDurationMinutes(requestDto.getEstimatedDurationMinutes());
        route.setBaseFare(requestDto.getBaseFare());
        route.setAgencyReferenceId(requestDto.getAgencyReferenceId());
        route.setIsActive(true);
        
        // Save route first
        route = routeRepository.save(route);
        
        // Add stops if provided
        if (requestDto.getStops() != null && !requestDto.getStops().isEmpty()) {
            Route finalRoute = route;
            List<RouteStop> routeStops = new ArrayList<>();
            
            for (RouteStopRequestDto stopDto : requestDto.getStops()) {
                Stop stop = getOrCreateStop(stopDto);
                
                RouteStop routeStop = new RouteStop();
                routeStop.setRoute(finalRoute);
                routeStop.setStop(stop);
                routeStop.setStopSequence(stopDto.getStopSequence());
                routeStop.setArrivalTimeOffsetMinutes(stopDto.getArrivalTimeOffsetMinutes());
                routeStop.setDepartureTimeOffsetMinutes(stopDto.getDepartureTimeOffsetMinutes());
                routeStop.setDistanceFromPreviousKm(stopDto.getDistanceFromPreviousKm());
                routeStop.setFareFromOrigin(stopDto.getFareFromOrigin());
                routeStop.setIsBoardingPoint(stopDto.getIsBoardingPoint());
                routeStop.setIsDroppingPoint(stopDto.getIsDroppingPoint());
                
                routeStops.add(routeStop);
            }
            
            route.getRouteStops().addAll(routeStops);
            route = routeRepository.save(route);
        }
        
        log.info("Route created with referenceId: {}", route.getReferenceId());
        return toRouteResponseDto(route);
    }
    
    @Override
    public RouteResponseDto updateRoute(UUID referenceId, RouteRequestDto requestDto) {
        log.info("Updating route: {}", referenceId);
        
        Route route = routeRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        // Check if new name conflicts with existing routes
        if (!route.getRouteName().equals(requestDto.getRouteName()) &&
                routeRepository.existsByRouteNameAndAgencyReferenceId(
                        requestDto.getRouteName(), requestDto.getAgencyReferenceId())) {
            throw new BusinessException("Route with this name already exists for the agency");
        }
        
        route.setRouteName(requestDto.getRouteName());
        route.setSourceCity(requestDto.getSourceCity());
        route.setDestinationCity(requestDto.getDestinationCity());
        route.setDistanceKm(requestDto.getDistanceKm());
        route.setEstimatedDurationMinutes(requestDto.getEstimatedDurationMinutes());
        route.setBaseFare(requestDto.getBaseFare());
        
        // Clear existing stops and add new ones if provided
        if (requestDto.getStops() != null) {
            route.getRouteStops().clear();
            
            for (RouteStopRequestDto stopDto : requestDto.getStops()) {
                Stop stop = getOrCreateStop(stopDto);
                
                RouteStop routeStop = new RouteStop();
                routeStop.setRoute(route);
                routeStop.setStop(stop);
                routeStop.setStopSequence(stopDto.getStopSequence());
                routeStop.setArrivalTimeOffsetMinutes(stopDto.getArrivalTimeOffsetMinutes());
                routeStop.setDepartureTimeOffsetMinutes(stopDto.getDepartureTimeOffsetMinutes());
                routeStop.setDistanceFromPreviousKm(stopDto.getDistanceFromPreviousKm());
                routeStop.setFareFromOrigin(stopDto.getFareFromOrigin());
                routeStop.setIsBoardingPoint(stopDto.getIsBoardingPoint());
                routeStop.setIsDroppingPoint(stopDto.getIsDroppingPoint());
                
                route.getRouteStops().add(routeStop);
            }
        }
        
        route = routeRepository.save(route);
        return toRouteResponseDto(route);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RouteResponseDto getRouteById(UUID referenceId) {
        log.info("Fetching route: {}", referenceId);
        
        Route route = routeRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        return toRouteResponseDto(route);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RouteResponseDto getRouteWithStops(UUID referenceId) {
        log.info("Fetching route with stops: {}", referenceId);
        
        Route route = routeRepository.findByReferenceIdWithStops(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        RouteResponseDto dto = toRouteResponseDto(route);
        dto.setStops(route.getRouteStops().stream()
                .map(this::toRouteStopResponseDto)
                .collect(Collectors.toList()));
        
        return dto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RouteResponseDto> getRoutesByAgency(String agencyReferenceId) {
        log.info("Fetching routes for agency: {}", agencyReferenceId);
        
        return routeRepository.findByAgencyReferenceId(agencyReferenceId).stream()
                .map(this::toRouteResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RouteResponseDto> getRoutesByCities(String sourceCity, String destinationCity) {
        log.info("Fetching routes from {} to {}", sourceCity, destinationCity);
        
        return routeRepository.findBySourceCityAndDestinationCity(sourceCity, destinationCity).stream()
                .map(this::toRouteResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RouteResponseDto> getAllActiveRoutes() {
        log.info("Fetching all active routes");
        
        return routeRepository.findByIsActiveTrue().stream()
                .map(this::toRouteResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public void activateRoute(UUID referenceId) {
        log.info("Activating route: {}", referenceId);
        
        Route route = routeRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        route.setIsActive(true);
        routeRepository.save(route);
    }
    
    @Override
    public void deactivateRoute(UUID referenceId) {
        log.info("Deactivating route: {}", referenceId);
        
        Route route = routeRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        route.setIsActive(false);
        routeRepository.save(route);
    }
    
    @Override
    public void deleteRoute(UUID referenceId) {
        log.info("Deleting route: {}", referenceId);
        
        Route route = routeRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found"));
        
        routeRepository.delete(route);
    }
    
    private Stop getOrCreateStop(RouteStopRequestDto stopDto) {
        // If stopId is provided, use existing stop
        if (stopDto.getStopId() != null) {
            return stopRepository.findByReferenceId(stopDto.getStopId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stop not found: " + stopDto.getStopId()));
        }
        
        // Otherwise, find or create new stop
        if (stopDto.getStopName() == null || stopDto.getCity() == null || stopDto.getState() == null) {
            throw new BusinessException("Stop details are required when stopId is not provided");
        }
        
        return stopRepository.findByStopNameAndCityAndState(
                stopDto.getStopName(), stopDto.getCity(), stopDto.getState())
                .orElseGet(() -> {
                    Stop newStop = new Stop();
                    newStop.setStopName(stopDto.getStopName());
                    newStop.setCity(stopDto.getCity());
                    newStop.setState(stopDto.getState());
                    newStop.setAddress(stopDto.getAddress());
                    newStop.setLatitude(stopDto.getLatitude());
                    newStop.setLongitude(stopDto.getLongitude());
                    newStop.setLandmark(stopDto.getLandmark());
                    newStop.setIsActive(true);
                    return stopRepository.save(newStop);
                });
    }
    
    private RouteResponseDto toRouteResponseDto(Route route) {
        return RouteResponseDto.builder()
                .referenceId(route.getReferenceId())
                .routeName(route.getRouteName())
                .sourceCity(route.getSourceCity())
                .destinationCity(route.getDestinationCity())
                .distanceKm(route.getDistanceKm())
                .estimatedDurationMinutes(route.getEstimatedDurationMinutes())
                .baseFare(route.getBaseFare())
                .isActive(route.getIsActive())
                .agencyReferenceId(route.getAgencyReferenceId())
                .stopCount(route.getRouteStops() != null ? route.getRouteStops().size() : 0)
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();
    }
    
    private RouteStopResponseDto toRouteStopResponseDto(RouteStop routeStop) {
        Stop stop = routeStop.getStop();
        return RouteStopResponseDto.builder()
                .referenceId(routeStop.getReferenceId())
                .stopReferenceId(stop.getReferenceId())
                .stopName(stop.getStopName())
                .city(stop.getCity())
                .state(stop.getState())
                .address(stop.getAddress())
                .landmark(stop.getLandmark())
                .stopSequence(routeStop.getStopSequence())
                .arrivalTimeOffsetMinutes(routeStop.getArrivalTimeOffsetMinutes())
                .departureTimeOffsetMinutes(routeStop.getDepartureTimeOffsetMinutes())
                .distanceFromPreviousKm(routeStop.getDistanceFromPreviousKm())
                .fareFromOrigin(routeStop.getFareFromOrigin())
                .isBoardingPoint(routeStop.getIsBoardingPoint())
                .isDroppingPoint(routeStop.getIsDroppingPoint())
                .build();
    }
}
