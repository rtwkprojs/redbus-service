package com.redbus.agency.entity;

public enum VehicleType {
    SEATER("Seater"),
    SLEEPER("Sleeper"),
    SEMI_SLEEPER("Semi Sleeper"),
    VOLVO("Volvo"),
    AC_SEATER("AC Seater"),
    NON_AC_SEATER("Non-AC Seater"),
    DELUXE("Deluxe"),
    LUXURY("Luxury");
    
    private final String displayName;
    
    VehicleType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
