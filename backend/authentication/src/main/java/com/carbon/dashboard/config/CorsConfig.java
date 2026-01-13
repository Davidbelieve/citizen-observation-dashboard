package com.carbon.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 * Allows React frontend running on port 3000 to communicate with backend on port 8080.
 * 
 * Without CORS configuration, browsers block requests from different origins
 * due to Same-Origin Policy security restrictions.
 * 
 * Configuration Details:
 * - Allowed Origin: http://localhost:3000 (React development server)
 * - Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
 * - Allowed Headers: All headers including Authorization (for JWT)
 * - Credentials: Enabled (allows cookies and Authorization headers)
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    /**
     * Configures CORS mappings for the application.
     * Applies to all endpoints under /api/ path.
     * 
     * Why we need this:
     * - Frontend: http://localhost:3000 and http://localhost:5173 (React)
     * - Backend: http://localhost:8080 (Spring Boot)
     * - Different ports = Different origins
     * - Browser blocks by default = CORS needed
     * 
     * @param registry the CORS registry to configure
     */
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Apply CORS to all /api/ endpoints
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")  // Allow React frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // HTTP methods
                .allowedHeaders("*")  // Allow all headers including Authorization
                .allowCredentials(true);  // Allow credentials (JWT tokens)
    }
    
    /**
     * Provides CORS configuration source for Spring Security.
     * This bean is required by SecurityConfig to handle CORS at the security filter level.
     * 
     * Important for:
     * - Handling OPTIONS preflight requests
     * - Working with Postman and other API clients
     * - Ensuring CORS headers are applied before security filters
     * 
     * @return CorsConfigurationSource configured for /api/** endpoints
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use allowedOriginPatterns to allow all origins (works with credentials)
        // This is more permissive for Postman and other API testing tools
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));  // Allow all headers including Authorization
        configuration.setAllowCredentials(true);  // Allow credentials (JWT tokens)
        configuration.setMaxAge(3600L);  // Cache preflight response for 1 hour
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
    
    /**
     * RestTemplate bean for making HTTP requests to microservices.
     * Used by GatewayController to forward requests.
     * Configured with timeouts to prevent hanging requests.
     * 
     * @return RestTemplate instance with timeout configuration
     */
    @Bean
    public RestTemplate restTemplate() {
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = 
            new org.springframework.http.client.SimpleClientHttpRequestFactory();
        
        // Set connection timeout (time to establish connection)
        factory.setConnectTimeout(5000); // 5 seconds
        
        // Set read timeout (time to wait for response)
        factory.setReadTimeout(10000); // 10 seconds
        
        return new RestTemplate(factory);
    }
}