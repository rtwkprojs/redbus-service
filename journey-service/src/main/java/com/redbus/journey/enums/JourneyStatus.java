package com.redbus.journey.enums;

public enum JourneyStatus {
    SCHEDULED("Scheduled"),
    BOARDING("Boarding"),
    DEPARTED("Departed"),
    IN_TRANSIT("In Transit"),
    ARRIVED("Arrived"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    DELAYED("Delayed");
    
    private final String displayName;
    
    JourneyStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
