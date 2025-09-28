package com.redbus.journey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.redbus.journey", "com.redbus.common"})
public class JourneyServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JourneyServiceApplication.class, args);
    }
}
