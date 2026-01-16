package com.waterquality.api_gateway.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class GatewayController {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // Service URLs
    private static final String CROWDSOURCED_SERVICE = "http://localhost:8080/api/submissions";
    private static final String REWARDS_SERVICE = "http://localhost:8081/api/rewards";
    
    /**
     * Route submission requests to Crowdsourced Data service
     */
    @PostMapping("/submissions")
    public ResponseEntity<?> submitObservation(@RequestBody String body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            
            return restTemplate.postForEntity(CROWDSOURCED_SERVICE, request, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\": \"Crowdsourced Data service unavailable\"}");
        }
    }
    
    /**
     * Route get all submissions to Crowdsourced Data service
     */
    @GetMapping("/submissions")
    public ResponseEntity<?> getAllSubmissions() {
        try {
            return restTemplate.getForEntity(CROWDSOURCED_SERVICE, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\": \"Crowdsourced Data service unavailable\"}");
        }
    }
    
    /**
     * Route rewards requests to Rewards service
     */
    @GetMapping("/rewards/citizen/{citizenId}")
    public ResponseEntity<?> getCitizenRewards(@PathVariable String citizenId) {
        try {
            String url = REWARDS_SERVICE + "/citizen/" + citizenId;
            return restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\": \"Rewards service unavailable\"}");
        }
    }
    
    /**
     * Route get all rewards to Rewards service
     */
    @GetMapping("/rewards")
    public ResponseEntity<?> getAllRewards() {
        try {
            return restTemplate.getForEntity(REWARDS_SERVICE, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\": \"Rewards service unavailable\"}");
        }
    }
}
