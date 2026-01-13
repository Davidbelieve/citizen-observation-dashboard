package com.carbon.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Carbon Intensity Dashboard.
 * This is the entry point of the Spring Boot application.
 * 
 * The application provides:
 * - User authentication with JWT tokens
 * - Password encryption using BCrypt
 * - REST API for Carbon Intensity data from UK API
 * - H2 in-memory database for user accounts
 * - CORS configuration for React frontend
 * 
 * Port: 8080 (configured in application.properties)
 * 
 * To run:
 * - In Eclipse: Right-click → Run As → Java Application
 * - In Terminal: mvn spring-boot:run
 * 
 * Access Points:
 * - API Base: http://localhost:8080/api
 * - H2 Console: http://localhost:8080/h2-console
 * - Health Check: http://localhost:8080/actuator/health (if actuator enabled)
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@SpringBootApplication
public class CarbonDashboardApplication {
    
    /**
     * Main method - entry point of the application.
     * Starts the embedded Tomcat server and initializes Spring context.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CarbonDashboardApplication.class, args);
        System.out.println("=========================================");
        System.out.println("Carbon Intensity Dashboard API Started");
        System.out.println("=========================================");
        System.out.println("API running on: http://localhost:8080");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("=========================================");
    }
}