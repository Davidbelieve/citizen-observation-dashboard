package com.carbon.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Gateway controller that routes authenticated requests to appropriate microservices.
 * All requests must include a valid JWT token in the Authorization header.
 * 
 * Routing Pattern:
 * - /api/regions/{region}/** → Routes to appropriate microservice
 * 
 * Example:
 * - /api/regions/north-west-england/observations → http://localhost:8080/api/observations
 * - /api/regions/yorkshire/crowd/count → http://localhost:8086/citizenscience/crowd/count
 * - /api/regions/south-east-england/observations → http://localhost:8091/api/v1/observations
 * - /api/regions/south-east-england/rewards → http://localhost:8092/api/v1/rewards
 */
@RestController
@RequestMapping("/api/regions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class GatewayController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    // Region to microservice base URL mapping
    private static final Map<String, String> REGION_MICROSERVICE_MAP = new HashMap<>();
    
    static {
        REGION_MICROSERVICE_MAP.put("north-east-england", "http://localhost:8081");
        REGION_MICROSERVICE_MAP.put("north-west-england", "http://localhost:8080/api");
        REGION_MICROSERVICE_MAP.put("east-midlands", "http://localhost:8083");
        REGION_MICROSERVICE_MAP.put("west-midlands", "http://localhost:8084");
        REGION_MICROSERVICE_MAP.put("south-east-england", "http://localhost:8091/api/v1");
        REGION_MICROSERVICE_MAP.put("yorkshire", "http://localhost:8086/citizenscience");
    }
    
    /**
     * Routes requests to appropriate microservice based on region.
     * 
     * Pattern: /api/regions/{region}/** 
     * 
     * Examples:
     * - GET /api/regions/north-west-england/observations
     * - GET /api/regions/yorkshire/crowd/count
     * - GET /api/regions/oluwabusola/observations?page=0&size=5
     */
    @RequestMapping(value = "/{region}/**", method = {RequestMethod.GET, RequestMethod.POST, 
                                                      RequestMethod.PUT, RequestMethod.DELETE, 
                                                      RequestMethod.PATCH})
    public ResponseEntity<?> routeRequest(
            @PathVariable String region,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Object requestBody,
            HttpMethod method,
            HttpServletRequest request) {
        
        // Get the microservice base URL for this region
        String microserviceBaseUrl = REGION_MICROSERVICE_MAP.get(region);
        
        if (microserviceBaseUrl == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Unknown region: " + region));
        }
        
        // Extract the path after /api/regions/{region}/
        String requestPath = request.getRequestURI();
        int regionIndex = requestPath.indexOf(region);
        String pathAfterRegion = requestPath.substring(regionIndex + region.length() + 1);
        
        // Build the target URL
        String targetUrl = microserviceBaseUrl;
        if (!targetUrl.endsWith("/") && !pathAfterRegion.startsWith("/")) {
            targetUrl += "/";
        }
        targetUrl += pathAfterRegion;
        
        // Add query parameters if any
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            targetUrl += "?" + queryString;
        }
        
        // Prepare headers (forward Authorization and other headers)
        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null) {
            headers.set("Authorization", authHeader);
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Copy other headers (except host and content-length)
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            if (!headerName.equalsIgnoreCase("host") && 
                !headerName.equalsIgnoreCase("content-length") &&
                !headerName.equalsIgnoreCase("authorization")) {
                headers.set(headerName, request.getHeader(headerName));
            }
        });
        
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            // Forward the request to the microservice
            ResponseEntity<Object> response = restTemplate.exchange(
                targetUrl,
                method,
                entity,
                Object.class
            );
            
            return ResponseEntity.status(response.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
                    
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString() != null ? 
                          parseErrorResponse(e.getResponseBodyAsString()) : 
                          Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Failed to route request: " + e.getMessage()));
        }
    }
    
    private Object parseErrorResponse(String responseBody) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(responseBody, Object.class);
        } catch (Exception e) {
            return Map.of("error", responseBody);
        }
    }
}

