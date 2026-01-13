package com.waterQualityMonitoring.reward;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Rewards microservice.
 * <p>
 * Calculates citizen reward points and badges based on validated crowdsourced
 * observations.
 * </p>
 */
@SpringBootApplication
public class RewardApplication {

    /**
     * Boots the Spring application context.
     *
     * @param args optional command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RewardApplication.class, args);
    }

}
