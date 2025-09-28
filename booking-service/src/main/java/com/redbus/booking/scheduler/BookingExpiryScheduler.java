package com.redbus.booking.scheduler;

import com.redbus.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryScheduler {
    
    private final BookingService bookingService;
    
    /**
     * Run every 5 minutes to process expired bookings
     */
    @Scheduled(fixedDelay = 300000, initialDelay = 60000) // 5 minutes delay, 1 minute initial delay
    public void processExpiredBookings() {
        log.info("Starting scheduled task to process expired bookings");
        try {
            bookingService.processExpiredBookings();
            log.info("Completed processing expired bookings");
        } catch (Exception e) {
            log.error("Error processing expired bookings", e);
        }
    }
}
