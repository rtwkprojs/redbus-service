package com.redbus.journey.entity;

import com.redbus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Route extends BaseEntity {
    
    @Column(name = "route_name", nullable = false, length = 200)
    private String routeName;
    
    @Column(name = "source_city", nullable = false, length = 100)
    private String sourceCity;
    
    @Column(name = "destination_city", nullable = false, length = 100)
    private String destinationCity;
    
    @Column(name = "distance_km", nullable = false)
    private Integer distanceKm;
    
    @Column(name = "estimated_duration_minutes", nullable = false)
    private Integer estimatedDurationMinutes;
    
    @Column(name = "base_fare", nullable = false)
    private Double baseFare;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "agency_reference_id", nullable = false)
    private String agencyReferenceId;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("stopSequence ASC")
    private List<RouteStop> routeStops = new ArrayList<>();
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Journey> journeys = new ArrayList<>();
}
