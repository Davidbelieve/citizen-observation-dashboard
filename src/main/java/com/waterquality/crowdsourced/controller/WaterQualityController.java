package com.waterquality.crowdsourced.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.waterquality.crowdsourced.model.WaterQualitySubmission;
import com.waterquality.crowdsourced.repository.SubmissionRepository;
import com.waterquality.crowdsourced.service.ValidationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WaterQualityController {
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private SubmissionRepository repository;
    
    /**
     * Submit a new water quality observation
     */
    @PostMapping("/submissions")
    public ResponseEntity<?> submitObservation(@RequestBody WaterQualitySubmission submission) {
        try {
            if (!validationService.isValid(submission)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid submission");
                error.put("message", "Submission must include postcode and at least one measurement or observation");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            submission.setCitizenId("CITIZEN_001");
            WaterQualitySubmission saved = repository.save(submission);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Get all submissions
     */
    @GetMapping("/submissions")
    public ResponseEntity<List<WaterQualitySubmission>> getAllSubmissions() {
        return ResponseEntity.ok(repository.findAll());
    }
    
    /**
     * Get submissions by citizen ID
     */
    @GetMapping("/submissions/citizen/{citizenId}")
    public ResponseEntity<List<WaterQualitySubmission>> getSubmissionsByCitizen(@PathVariable String citizenId) {
        return ResponseEntity.ok(repository.findByCitizenId(citizenId));
    }
    
    /**
     * Get count of all observations
     * Frontend expects: GET /observations/count?region={region}
     */
    @GetMapping("/observations/count")
    public ResponseEntity<Map<String, Integer>> getObservationCount(@RequestParam(required = false) String region) {
        Map<String, Integer> response = new HashMap<>();
        response.put("count", (int) repository.count());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get recent observations
     * Frontend expects: GET /observations/recent?region={region}&limit=5
     */
    @GetMapping("/observations/recent")
    public ResponseEntity<Map<String, List<WaterQualitySubmission>>> getRecentObservations(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "5") int limit) {
        
        List<WaterQualitySubmission> submissions = repository.findAll();
        
        List<WaterQualitySubmission> recent = submissions.stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .limit(limit)
                .toList();
        
        Map<String, List<WaterQualitySubmission>> response = new HashMap<>();
        response.put("observations", recent);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get leaderboard of top contributors
     * Frontend expects: GET /contributors/leaderboard?region={region}&limit=3
     */
    @GetMapping("/contributors/leaderboard")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getLeaderboard(
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "3") int limit) {
        
        List<WaterQualitySubmission> submissions = repository.findAll();
        Map<String, Long> contributorCounts = new HashMap<>();
        
        for (WaterQualitySubmission sub : submissions) {
            String citizenId = sub.getCitizenId();
            contributorCounts.put(citizenId, contributorCounts.getOrDefault(citizenId, 0L) + 1);
        }
        
        List<Map<String, Object>> leaderboard = contributorCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> contributor = new HashMap<>();
                    contributor.put("id", entry.getKey());
                    contributor.put("username", entry.getKey());
                    contributor.put("points", entry.getValue() * 10);
                    return contributor;
                })
                .toList();
        
        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("contributors", leaderboard);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Add test data for development
     */
    @PostMapping("/test/seed")
    public ResponseEntity<String> seedTestData() {
        // Create 5 test submissions
        for (int i = 1; i <= 5; i++) {
            WaterQualitySubmission sub = new WaterQualitySubmission();
            sub.setPostcode("NW" + i + " 4ST");
            sub.setCitizenId("CITIZEN_00" + i);
            sub.setObservations("Clear water with normal pH levels - Test observation " + i);
            sub.setTemperature(15.0 + i);
            sub.setPh(7.0 + (i * 0.1));
            sub.setAlkalinity(100.0 + (i * 5));
            sub.setTurbidity(2.0 + (i * 0.5));
            sub.setTimestamp(java.time.LocalDateTime.now().minusDays(i));
            repository.save(sub);
        }
        return ResponseEntity.ok("Test data added: 5 water quality observations");
    }
}