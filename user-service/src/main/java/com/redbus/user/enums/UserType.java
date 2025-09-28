package com.redbus.user.enums;

public enum UserType {
    CUSTOMER("Customer"),
    AGENCY_ADMIN("Agency Admin");
    
    private final String displayName;
    
    UserType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
