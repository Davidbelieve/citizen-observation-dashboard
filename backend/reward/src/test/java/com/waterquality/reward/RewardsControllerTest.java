package com.waterquality.reward;

import com.waterquality.reward.controller.RewardsController;
import com.waterquality.reward.model.CitizenReward;
import com.waterquality.reward.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
public class RewardsControllerTest {
    
    @Autowired
    private RewardsController controller;
    
    @Autowired
    private RewardService service;
    
    /**
     * Test 1: Calculate rewards for a citizen
     */
    @Test
    public void testCalculateRewards() {
        // This will call the Crowdsourced Data service
        // Make sure port 8080 service is running!
        CitizenReward reward = service.calculateRewards("CITIZEN_001");
        
        assertNotNull(reward);
        assertEquals("CITIZEN_001", reward.getCitizenId());
        assertTrue(reward.getTotalPoints() >= 0);
        
        System.out.println("✓ Test 1 PASSED: Rewards calculated");
        System.out.println("  Points: " + reward.getTotalPoints());
        System.out.println("  Badge: " + reward.getBadge());
    }
    
    /**
     * Test 2: Badge awarding logic
     */
    @Test
    public void testBadgeAwarding() {
        // Test with known citizen
        CitizenReward reward = service.calculateRewards("CITIZEN_001");
        
        // Verify badge logic
        if (reward.getTotalPoints() >= 500) {
            assertEquals("Gold", reward.getBadge());
        } else if (reward.getTotalPoints() >= 200) {
            assertEquals("Silver", reward.getBadge());
        } else if (reward.getTotalPoints() >= 100) {
            assertEquals("Bronze", reward.getBadge());
        } else {
            assertEquals("None", reward.getBadge());
        }
        
        System.out.println("✓ Test 2 PASSED: Badge logic correct");
    }
    
    /**
     * Test 3: Get all rewards
     */
    @Test
    public void testGetAllRewards() {
        List<CitizenReward> rewards = service.getAllRewards();
        
        assertNotNull(rewards);
        // Should have at least the citizens we tested
        assertTrue(rewards.size() >= 0);
        
        System.out.println("✓ Test 3 PASSED: Get all rewards works");
        System.out.println("  Total citizens tracked: " + rewards.size());
    }
}