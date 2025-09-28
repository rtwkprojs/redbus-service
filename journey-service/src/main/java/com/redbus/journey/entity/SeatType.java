package com.redbus.journey.entity;

public enum SeatType {
    LOWER("Lower"),
    UPPER("Upper"),
    SEATER("Seater"),
    SLEEPER("Sleeper"),
    SEMI_SLEEPER("Semi Sleeper");
    
    private final String displayName;
    
    SeatType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
