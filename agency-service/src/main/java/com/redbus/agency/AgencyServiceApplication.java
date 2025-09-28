package com.redbus.agency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.redbus.agency", "com.redbus.common"})
public class AgencyServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AgencyServiceApplication.class, args);
    }
}
