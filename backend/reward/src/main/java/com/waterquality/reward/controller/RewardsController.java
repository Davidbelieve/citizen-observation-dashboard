package com.waterquality.reward.controller;

import com.waterquality.reward.model.CitizenReward;
import com.waterquality.reward.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {
    
    @Autowired
    private RewardService rewardService;
    
    /**
     * Get rewards for a specific citizen
     */
    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<?> getCitizenRewards(@PathVariable String citizenId) {
        try {
            CitizenReward reward = rewardService.calculateRewards(citizenId);
            return ResponseEntity.ok(reward);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error calculating rewards");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * Get all citizen rewards (leaderboard)
     */
    @GetMapping
    public ResponseEntity<List<CitizenReward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }
}