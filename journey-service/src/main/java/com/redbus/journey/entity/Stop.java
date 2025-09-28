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
@Table(name = "stops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stop extends BaseEntity {
    
    @Column(name = "stop_name", nullable = false, length = 100)
    private String stopName;
    
    @Column(name = "city", nullable = false, length = 100)
    private String city;
    
    @Column(name = "state", nullable = false, length = 100)
    private String state;
    
    @Column(name = "address", length = 200)
    private String address;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "landmark", length = 100)
    private String landmark;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "stop", fetch = FetchType.LAZY)
    private List<RouteStop> routeStops = new ArrayList<>();
}
