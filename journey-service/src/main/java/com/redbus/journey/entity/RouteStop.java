package com.redbus.journey.entity;

import com.redbus.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "route_stops",
    uniqueConstraints = @UniqueConstraint(columnNames = {"route_id", "stop_sequence"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteStop extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;
    
    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;
    
    @Column(name = "arrival_time_offset_minutes", nullable = false)
    private Integer arrivalTimeOffsetMinutes; // Minutes from journey start
    
    @Column(name = "departure_time_offset_minutes", nullable = false)
    private Integer departureTimeOffsetMinutes; // Minutes from journey start
    
    @Column(name = "distance_from_previous_km")
    private Integer distanceFromPreviousKm;
    
    @Column(name = "fare_from_origin")
    private Double fareFromOrigin;
    
    @Column(name = "is_boarding_point", nullable = false)
    private Boolean isBoardingPoint = true;
    
    @Column(name = "is_dropping_point", nullable = false)
    private Boolean isDroppingPoint = true;
}
