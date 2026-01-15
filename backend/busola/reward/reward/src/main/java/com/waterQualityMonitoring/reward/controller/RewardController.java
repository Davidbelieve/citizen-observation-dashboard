package com.waterQualityMonitoring.reward.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waterQualityMonitoring.reward.dto.RewardCalculationResult;
import com.waterQualityMonitoring.reward.dto.RewardSummaryResponse;
import com.waterQualityMonitoring.reward.service.RewardService;
import com.waterQualityMonitoring.reward.service.exception.ResourceNotFoundException;

/**
 * REST controller exposing endpoints to query and recalculate citizen rewards.
 */
@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {

    private final RewardService rewardService;

    /**
     * Creates the controller backed by {@link RewardService}.
     *
     * @param rewardService service orchestrating reward calculations
     */
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Returns reward summaries for all citizens that have earned points.
     *
     * @return 200 with summaries or 204 when no rewards have been calculated
     */
    @GetMapping
    public ResponseEntity<List<RewardSummaryResponse>> getAllRewards() {
        List<RewardSummaryResponse> summaries = rewardService.getAllSummaries();
        if (CollectionUtils.isEmpty(summaries)) {
            RewardCalculationResult calculationResult = rewardService.calculateRewards();
            summaries = calculationResult.getSummaries();
        }
        if (CollectionUtils.isEmpty(summaries)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(summaries);
    }

    /**
     * Retrieves the reward summary for a specific citizen.
     *
     * @param citizenId unique citizen identifier
     * @return 200 with reward data or 404 if no rewards exist
     */
    @GetMapping("/{citizenId}")
    public ResponseEntity<RewardSummaryResponse> getRewardByCitizen(@PathVariable String citizenId) {
        Optional<RewardSummaryResponse> summary = rewardService.getSummaryForCitizen(citizenId);
        return summary.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No rewards found for citizen " + citizenId));
    }

    /**
     * Triggers recalculation of rewards,
     *
     * @param request optional request body supplying custom observations
     * @return 201 response containing the recalculated summaries and warnings
     */
    @PostMapping("/calculate")
    public ResponseEntity<RewardCalculationResult> calculateRewards() {
        RewardCalculationResult result = rewardService.calculateRewards();
        return ResponseEntity.created(URI.create("/api/v1/rewards")).body(result);
    }
}
