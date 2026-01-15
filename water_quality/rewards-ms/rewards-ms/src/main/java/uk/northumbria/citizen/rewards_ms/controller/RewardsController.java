package uk.northumbria.citizen.rewards_ms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.northumbria.citizen.rewards_ms.dto.RewardResponse;
import uk.northumbria.citizen.rewards_ms.service.RewardsService;

/**
 * REST controller for rewards endpoints.
 * Exposes the /api/rewards/{citizenId} endpoint for retrieving citizen rewards.
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/rewards")
public class RewardsController {
    
    private static final Logger logger = LoggerFactory.getLogger(RewardsController.class);
    
    private final RewardsService rewardsService;
    
    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }
    
    /**
     * GET endpoint to retrieve rewards for a specific citizen.
     * 
     * @param citizenId The ID of the citizen
     * @return RewardResponse containing points and badge
     */
    @GetMapping("/{citizenId}")
    public ResponseEntity<RewardResponse> getRewardsByCitizenId(@PathVariable String citizenId) {
        logger.info("Received request for rewards for citizenId: {}", citizenId);
        RewardResponse rewardResponse = rewardsService.getRewardsByCitizenId(citizenId);
        logger.info("Returning rewards response: points={}, badge={}", 
            rewardResponse.getPoints(), rewardResponse.getBadge());
        return ResponseEntity.ok(rewardResponse);
    }
}

