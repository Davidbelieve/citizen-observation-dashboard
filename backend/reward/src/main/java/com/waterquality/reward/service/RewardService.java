package com.waterquality.reward.service;

import com.waterquality.reward.model.CitizenReward;
import com.waterquality.reward.model.WaterQualitySubmission;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RewardService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, CitizenReward> rewards = new ConcurrentHashMap<>();
    
    private static final String CROWDSOURCED_API = "http://localhost:8080/api/submissions/citizen/";
    
    /**
     * Calculate rewards for a specific citizen
     */
    public CitizenReward calculateRewards(String citizenId) {
        // Get or create citizen reward record
        CitizenReward reward = rewards.getOrDefault(citizenId, new CitizenReward(citizenId));
        
        try {
            // Fetch submissions from Crowdsourced Data API
            String url = CROWDSOURCED_API + citizenId;
            WaterQualitySubmission[] submissions = restTemplate.getForObject(url, WaterQualitySubmission[].class);
            
            if (submissions != null) {
                int totalPoints = 0;
                
                // Calculate points for each submission
                for (WaterQualitySubmission sub : submissions) {
                    totalPoints += 10;  // Base points
                    
                    // Bonus for complete submissions
                    if (isComplete(sub)) {
                        totalPoints += 10;
                    }
                }
                
                reward.setTotalPoints(totalPoints);
                reward.setSubmissionCount(submissions.length);
                reward.setBadge(determineBadge(totalPoints));
                
                // Save updated reward
                rewards.put(citizenId, reward);
            }
            
        } catch (Exception e) {
            // Handle API call failures gracefully
            System.err.println("Error fetching submissions: " + e.getMessage());
        }
        
        return reward;
    }
    
    /**
     * Check if submission is complete (all fields filled)
     */
    private boolean isComplete(WaterQualitySubmission sub) {
        return sub.getPostcode() != null &&
               sub.getTemperature() != null &&
               sub.getPh() != null &&
               sub.getAlkalinity() != null &&
               sub.getTurbidity() != null &&
               sub.getObservations() != null &&
               !sub.getObservations().trim().isEmpty();
    }
    
    /**
     * Determine badge based on points
     */
    private String determineBadge(int points) {
        if (points >= 500) return "Gold";
        if (points >= 200) return "Silver";
        if (points >= 100) return "Bronze";
        return "None";
    }
    
    /**
     * Get all citizen rewards
     */
    public List<CitizenReward> getAllRewards() {
        return new ArrayList<>(rewards.values());
    }
} 