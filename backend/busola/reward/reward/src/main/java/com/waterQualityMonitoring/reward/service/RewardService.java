package com.waterQualityMonitoring.reward.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.waterQualityMonitoring.reward.dto.CrowdsourcedObservation;
import com.waterQualityMonitoring.reward.dto.RewardCalculationResult;
import com.waterQualityMonitoring.reward.dto.RewardSummaryResponse;
import com.waterQualityMonitoring.reward.model.BadgeLevel;
import com.waterQualityMonitoring.reward.model.RewardModel;
import com.waterQualityMonitoring.reward.repository.RewardRepository;

/**
 * Core service responsible for calculating reward points and badges for
 * citizens.
 */
@Service
public class RewardService {

    private static final int BASE_POINTS = 10;
    private static final int COMPLETENESS_BONUS = 10;

    private final CrowdsourcedObservationClient observationClient;
    private final RewardRepository rewardRepository;

    /**
     * Creates a service instance backed by the observation client and in-memory
     * reward repository.
     *
     * @param observationClient source of validated observations
     * @param rewardRepository  repository used to persist calculation results
     */
    public RewardService(CrowdsourcedObservationClient observationClient, RewardRepository rewardRepository) {
        this.observationClient = observationClient;
        this.rewardRepository = rewardRepository;
    }

    /**
     * Recomputes rewards for all citizens based on observations supplied by the
     * crowdsourced service.
     *
     * @return result containing reward summaries and warning messages
     */
    public RewardCalculationResult calculateRewards() {
        List<CrowdsourcedObservation> observations = observationClient.fetchValidatedObservations();

        rewardRepository.clear();

        Map<String, Integer> pointsByCitizen = new HashMap<>();
        List<String> warnings = new ArrayList<>();

        if (observations.isEmpty()) {
            warnings.add("No observations available from crowdsourced service.");
            return new RewardCalculationResult(List.of(), warnings);
        }

        for (CrowdsourcedObservation observation : observations) {
            String citizenId = observation.getCitizenId();
            if (!StringUtils.hasText(citizenId)) {
                warnings.add(buildMissingCitizenWarning(observation));
                continue;
            }
            int points = BASE_POINTS;
            if (isCompleteSubmission(observation)) {
                points += COMPLETENESS_BONUS;
            }
            pointsByCitizen.merge(citizenId, points, Integer::sum);
        }

        List<RewardSummaryResponse> summaries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : pointsByCitizen.entrySet()) {
            String citizenId = entry.getKey();
            int totalPoints = entry.getValue();
            BadgeLevel badgeLevel = BadgeLevel.fromPoints(totalPoints);
            RewardModel rewardModel = new RewardModel(citizenId, totalPoints, badgeLevel);
            rewardRepository.save(rewardModel);
            summaries.add(new RewardSummaryResponse(citizenId, totalPoints, badgeLevel));
        }

        if (summaries.isEmpty() && warnings.isEmpty()) {
            warnings.add("No rewards generated. Check citizen IDs on observations.");
        }

        return new RewardCalculationResult(summaries, warnings);
    }

    /**
     * Returns reward summaries for every citizen currently stored in the repository.
     *
     * @return list of reward summaries
     */
    public List<RewardSummaryResponse> getAllSummaries() {
        return rewardRepository.findAll().stream()
                .map(reward -> new RewardSummaryResponse(
                        reward.getCitizenId(),
                        reward.getTotalPoints(),
                        reward.getBadgeLevel()))
                .toList();
    }

    /**
     * Retrieves the reward summary for a specific citizen if available.
     *
     * @param citizenId unique citizen identifier
     * @return optional containing the reward summary
     */
    public Optional<RewardSummaryResponse> getSummaryForCitizen(String citizenId) {
        return rewardRepository.findByCitizenId(citizenId)
                .map(reward -> new RewardSummaryResponse(
                        reward.getCitizenId(),
                        reward.getTotalPoints(),
                        reward.getBadgeLevel()));
    }

    private boolean isCompleteSubmission(CrowdsourcedObservation observation) {
        return observation.hasCompleteMeasurement()
                && observation.hasTags();
    }

    private String buildMissingCitizenWarning(CrowdsourcedObservation observation) {
        String observationId = observation.getObservationId();
        if (StringUtils.hasText(observationId)) {
            return "Skipping observation " + observationId + " due to missing citizenId";
        }
        return "Skipping observation due to missing citizenId";
    }
}

