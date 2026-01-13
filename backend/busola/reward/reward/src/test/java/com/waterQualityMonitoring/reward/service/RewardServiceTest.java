package com.waterQualityMonitoring.reward.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.waterQualityMonitoring.reward.dto.CrowdsourcedObservation;
import com.waterQualityMonitoring.reward.dto.RewardCalculationResult;
import com.waterQualityMonitoring.reward.dto.RewardSummaryResponse;
import com.waterQualityMonitoring.reward.model.BadgeLevel;
import com.waterQualityMonitoring.reward.repository.RewardRepository;

class RewardServiceTest {

    private RewardRepository rewardRepository;
    private StubCrowdsourcedObservationClient stubClient;
    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        rewardRepository = new RewardRepository();
        stubClient = new StubCrowdsourcedObservationClient();
        rewardService = new RewardService(stubClient, rewardRepository);
    }

    @Test
    void calculateRewardsShouldPersistSummaries() {
        RewardCalculationResult result = rewardService.calculateRewards();

        assertThat(result.getSummaries()).hasSize(2);
        assertThat(result.getWarnings()).isEmpty();

        List<RewardSummaryResponse> summaries = rewardService.getAllSummaries();
        assertThat(summaries).hasSize(2);

        RewardSummaryResponse citizenOne = summaries.stream()
                .filter(summary -> summary.getCitizenId().equals("citizen-001"))
                .findFirst()
                .orElseThrow();
        assertThat(citizenOne.getTotalPoints()).isEqualTo(20);
        assertThat(citizenOne.getBadge()).isEqualTo(BadgeLevel.NONE.name());
    }

    @Test
    void calculateRewardsShouldReturnWarningsForMissingCitizen() {
        CrowdsourcedObservation invalidObservation = new CrowdsourcedObservation();
        invalidObservation.setValidated(true);
        stubClient.replaceObservations(List.of(invalidObservation));

        RewardCalculationResult result = rewardService.calculateRewards();

        assertThat(result.getSummaries()).isEmpty();
        assertThat(result.getWarnings()).hasSize(1);
        assertThat(result.getWarnings().get(0)).contains("missing citizenId");
        assertThat(rewardService.getAllSummaries()).isEmpty();
    }
}

