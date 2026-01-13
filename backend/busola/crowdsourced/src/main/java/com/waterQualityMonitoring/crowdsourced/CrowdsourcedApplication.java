package com.waterQualityMonitoring.crowdsourced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Crowdsourced Data microservice.
 * <p>
 * Boots the Spring context that exposes CRUD endpoints for water-quality
 * observations submitted by citizens. The service validates observations,
 * persists them and exposes projections used by downstream microservices.
 * </p>
 */
@SpringBootApplication
public class CrowdsourcedApplication {

    /**
     * Launches the Crowdsourced Data microservice.
     *
     * @param args optional command-line arguments for Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(CrowdsourcedApplication.class, args);
    }

}
