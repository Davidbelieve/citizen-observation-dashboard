package com.carbon.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Gateway controller that routes authenticated requests to appropriate microservices.
 * All requests must include a valid JWT token in the Authorization header.
 * 
 * Routing Patterns:
 * - /api/auth/** → Handled by AuthController (authentication endpoints)
 * - /api/v1/** → Routes to South East England Gateway (port 8090)
 *                Gateway (8090) then routes to:
 *                - /api/v1/observations/** → Crowdsourced Service (port 8091)
 *                - /api/v1/rewards/** → Reward Service (port 8092)
 * - /api/regions/{region}/** → Routes to region-specific microservices
 * 
 * Examples:
 * - /api/v1/observations → http://localhost:8090/api/v1/observations → gateway routes to 8091
 * - /api/v1/rewards → http://localhost:8090/api/v1/rewards → gateway routes to 8092
 * - /api/regions/north-west-england/observations → http://localhost:8082/api/observations (Lola)
 * - /api/regions/yorkshire/crowd/count → http://localhost:8086/citizenscience/crowd/count
 * - /api/regions/north-east-england/** → http://localhost:8081/**
 */
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class GatewayController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    // Post-construct to verify RestTemplate is injected
    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("GatewayController: RestTemplate injected: " + (restTemplate != null));
        if (restTemplate == null) {
            System.err.println("GatewayController: ERROR - RestTemplate is NULL!");
        }
    }
    
    // Gateway URL - All /api/v1/** requests go through this gateway
    // The gateway (port 8090) handles routing to 8091 (crowdsourced) and 8092 (rewards)
    private static final String SOUTH_EAST_GATEWAY = "http://localhost:8090";
    
    // Region to microservice base URL mapping
    private static final Map<String, String> REGION_MICROSERVICE_MAP = new HashMap<>();
    
    static {
        REGION_MICROSERVICE_MAP.put("north-east-england", "http://localhost:8081");
        REGION_MICROSERVICE_MAP.put("north-west-england", "http://localhost:8082/api");  // Lola's service
        REGION_MICROSERVICE_MAP.put("east-midlands", "http://localhost:8083");
        REGION_MICROSERVICE_MAP.put("west-midlands", "http://localhost:8084");
        REGION_MICROSERVICE_MAP.put("south-east-england", SOUTH_EAST_GATEWAY);  // Oluwabusola's gateway
        REGION_MICROSERVICE_MAP.put("yorkshire", "http://localhost:8086/citizenscience");
    }
    
    /**
     * OLUWABUSOLA'S (SOUTH EAST ENGLAND) ENDPOINT ROUTING LOGIC
     * Routes /api/v1/** requests to South East England Gateway (port 8090).
     * The gateway (8090) then handles routing to:
     * - /api/v1/observations/** → Crowdsourced Service (port 8091)
     * - /api/v1/rewards/** → Reward Service (port 8092)
     * 
     * Pattern: /api/v1/**
     * 
     * Examples:
     * - GET /api/v1/observations → http://localhost:8090/api/v1/observations → gateway routes to 8091
     * - POST /api/v1/observations → http://localhost:8090/api/v1/observations → gateway routes to 8091
     * - GET /api/v1/rewards → http://localhost:8090/api/v1/rewards → gateway routes to 8092
     * - GET /api/v1/rewards/leaderboard → http://localhost:8090/api/v1/rewards/leaderboard → gateway routes to 8092
     */
    @RequestMapping(value = "/api/v1/**", method = {RequestMethod.GET, RequestMethod.POST, 
                                                    RequestMethod.PUT, RequestMethod.DELETE, 
                                                    RequestMethod.PATCH, RequestMethod.OPTIONS})
    public ResponseEntity<?> routeV1Request(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) Object requestBody,
            HttpServletRequest request) {
        
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        
        if (method == HttpMethod.OPTIONS) {
            return ResponseEntity.ok().build();
        }
        
        String requestPath = request.getRequestURI();
        // Preserve the full path including /api/v1 for the gateway
        // Gateway expects /api/v1/** paths
        String targetUrl = SOUTH_EAST_GATEWAY + requestPath;
        
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            targetUrl += "?" + queryString;
        }
        
        System.out.println("GatewayController: [STEP 1] Routing /api/v1 request to: " + targetUrl);
        System.out.flush(); // Force flush to ensure log appears
        
        System.out.println("GatewayController: [STEP 2] About to call forwardRequest...");
        System.out.flush();
        
        System.out.println("GatewayController: [STEP 3] Parameters - targetUrl: " + targetUrl);
        System.out.println("GatewayController: [STEP 3] Parameters - authHeader null: " + (authHeader == null));
        System.out.println("GatewayController: [STEP 3] Parameters - requestBody null: " + (requestBody == null));
        System.out.println("GatewayController: [STEP 3] Parameters - method: " + method);
        System.out.flush();
        
        try {
            System.out.println("GatewayController: [STEP 4] Entering try block, calling forwardRequest...");
            System.out.flush();
            
            ResponseEntity<?> result = forwardRequest(targetUrl, authHeader, requestBody, method, request);
            
            System.out.println("GatewayController: [STEP 5] forwardRequest returned successfully!");
            System.out.flush();
            
            return result;
        } catch (Exception e) {
            System.err.println("GatewayController: [ERROR] EXCEPTION in routeV1Request!");
            System.err.println("GatewayController: [ERROR] Exception type: " + e.getClass().getName());
            System.err.println("GatewayController: [ERROR] Exception message: " + e.getMessage());
            System.err.println("GatewayController: [ERROR] Exception occurred at line before forwardRequest call");
            e.printStackTrace();
            System.err.flush();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * Routes requests to appropriate microservice based on region.
     * This handles routing for all regions including Lola's North West England service.
     * 
     * Pattern: /api/regions/{region}/** 
     * 
     * Examples:
     * - GET /api/regions/north-west-england/observations → http://localhost:8082/api/observations (Lola)
     * - GET /api/regions/yorkshire/crowd/count → http://localhost:8086/citizenscience/crowd/count
     * - GET /api/regions/south-east-england/observations → http://localhost:8090/api/v1/observations (Oluwabusola)
     */
    @RequestMapping(value = "/api/regions/{region}/**", method = {RequestMethod.GET, RequestMethod.POST, 
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
        
        return forwardRequest(targetUrl, authHeader, requestBody, method, request);
    }
    
    /**
     * Helper method to forward requests to target URLs.
     */
    private ResponseEntity<?> forwardRequest(String targetUrl, String authHeader, 
                                             Object requestBody, HttpMethod method, 
                                             HttpServletRequest request) {
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
            System.out.println("GatewayController: [FORWARD-4] About to call RestTemplate.exchange...");
            System.out.println("GatewayController: [FORWARD-4] URL: " + targetUrl);
            System.out.println("GatewayController: [FORWARD-4] Method: " + method);
            System.out.println("GatewayController: [FORWARD-4] RestTemplate instance: " + (restTemplate != null ? "NOT NULL" : "NULL"));
            System.out.flush();
            
            // Check if RestTemplate is null
            if (restTemplate == null) {
                System.err.println("GatewayController: [FORWARD-ERROR] RestTemplate is NULL! Cannot make request.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "RestTemplate not initialized"));
            }
            
            System.out.println("GatewayController: [FORWARD-5] RestTemplate is valid, calling exchange...");
            System.out.flush();
            
            // Forward the request to the microservice
            ResponseEntity<Object> response = restTemplate.exchange(
                targetUrl,
                method,
                entity,
                Object.class
            );
            
            // Create clean response headers, only including safe headers
            // This prevents "Invalid character in chunk size" errors in Vite proxy
            HttpHeaders responseHeaders = new HttpHeaders();
            
            // Only copy safe headers that won't cause HTTP parsing issues
            response.getHeaders().forEach((headerName, headerValues) -> {
                String lowerHeaderName = headerName.toLowerCase();
                // Exclude all connection-specific and transfer-encoding headers
                if (!lowerHeaderName.equals("transfer-encoding") &&
                    !lowerHeaderName.equals("content-length") &&
                    !lowerHeaderName.equals("connection") &&
                    !lowerHeaderName.equals("keep-alive") &&
                    !lowerHeaderName.equals("host") &&
                    !lowerHeaderName.equals("upgrade") &&
                    !lowerHeaderName.equals("proxy-connection") &&
                    !lowerHeaderName.equals("te") &&
                    !lowerHeaderName.equals("trailer")) {
                    // Copy the header
                    responseHeaders.put(headerName, headerValues);
                }
            });
            
            // Ensure Content-Type is set for JSON responses
            if (response.getBody() != null && !responseHeaders.containsKey("Content-Type")) {
                if (response.getHeaders().getContentType() != null) {
                    responseHeaders.setContentType(response.getHeaders().getContentType());
                } else {
                    responseHeaders.setContentType(MediaType.APPLICATION_JSON);
                }
            }
            
            // Let Spring automatically calculate Content-Length
            // Don't set it manually to avoid conflicts
            
            return ResponseEntity.status(response.getStatusCode())
                    .headers(responseHeaders)
                    .body(response.getBody());
                    
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Log client errors (4xx) - these are expected for bad requests
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString() != null ? 
                          parseErrorResponse(e.getResponseBodyAsString()) : 
                          Map.of("error", e.getMessage()));
        } catch (org.springframework.web.client.ResourceAccessException e) {
            // Log connection errors (gateway not running, connection refused, etc.)
            System.err.println("GatewayController: Cannot connect to gateway at " + targetUrl + ": " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("GatewayController: Cause: " + e.getCause().getMessage());
            }
            
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Cannot connect to gateway service. Please ensure the gateway is running on port 8090."));
        } catch (Exception e) {
            // Log unexpected errors
            System.err.println("GatewayController: Unexpected error forwarding to " + targetUrl);
            System.err.println("GatewayController: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            
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

