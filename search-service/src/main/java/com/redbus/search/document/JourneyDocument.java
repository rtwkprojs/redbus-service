package com.redbus.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "journeys")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JourneyDocument {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Text)
    private String journeyCode;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String sourceCity;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String destinationCity;
    
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime departureTime;
    
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime arrivalTime;
    
    @Field(type = FieldType.Double)
    private Double baseFare;
    
    @Field(type = FieldType.Integer)
    private Integer availableSeats;
    
    @Field(type = FieldType.Integer)
    private Integer totalSeats;
    
    @Field(type = FieldType.Text)
    private String agencyName;
    
    @Field(type = FieldType.Text)
    private String vehicleType;
    
    @Field(type = FieldType.Text)
    private String routeName;
    
    @Field(type = FieldType.Integer)
    private Integer durationMinutes;
    
    @Field(type = FieldType.Integer)
    private Integer distanceKm;
    
    @Field(type = FieldType.Keyword)
    private List<String> amenities;
    
    @Field(type = FieldType.Boolean)
    private Boolean isActive;
    
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
}
