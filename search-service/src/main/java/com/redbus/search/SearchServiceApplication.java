package com.redbus.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication(scanBasePackages = {"com.redbus.search", "com.redbus.common"})
@EnableElasticsearchRepositories
public class SearchServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
}
