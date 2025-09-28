package com.redbus.booking.enums;

public enum BookingStatus {
    INITIATED("Initiated"),
    SEATS_BLOCKED("Seats Blocked"),
    PAYMENT_PENDING("Payment Pending"),
    PAYMENT_PROCESSING("Payment Processing"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    FAILED("Failed"),
    EXPIRED("Expired"),
    REFUND_INITIATED("Refund Initiated"),
    REFUNDED("Refunded");
    
    private final String displayName;
    
    BookingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
