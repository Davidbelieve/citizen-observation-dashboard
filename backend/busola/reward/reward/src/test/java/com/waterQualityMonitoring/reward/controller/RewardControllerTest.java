package com.waterQualityMonitoring.reward.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.waterQualityMonitoring.reward.dto.RewardCalculationResult;
import com.waterQualityMonitoring.reward.dto.RewardSummaryResponse;
import com.waterQualityMonitoring.reward.model.BadgeLevel;
import com.waterQualityMonitoring.reward.service.RewardService;
import com.waterQualityMonitoring.reward.service.exception.CrowdsourcedServiceUnavailableException;

@WebMvcTest(controllers = RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Test
    void getAllRewardsShouldReturnNoContentWhenEmpty() throws Exception {
        when(rewardService.getAllSummaries()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/rewards"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllRewardsShouldReturnSummaries() throws Exception {
        RewardSummaryResponse summary = new RewardSummaryResponse("citizen-01", 150, BadgeLevel.BRONZE);
        when(rewardService.getAllSummaries()).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].citizenId").value("citizen-01"))
                .andExpect(jsonPath("$[0].badge").value("BRONZE"));
    }

    @Test
    void getRewardByCitizenShouldReturnNotFound() throws Exception {
        when(rewardService.getSummaryForCitizen("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/rewards/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.details").value("No rewards found for citizen missing"));
    }

    @Test
    void getRewardByCitizenShouldReturnSummary() throws Exception {
        RewardSummaryResponse summary = new RewardSummaryResponse("citizen-02", 200, BadgeLevel.SILVER);
        when(rewardService.getSummaryForCitizen("citizen-02")).thenReturn(Optional.of(summary));

        mockMvc.perform(get("/api/v1/rewards/citizen-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.badge").value("SILVER"));
    }

    @Test
    void calculateRewardsShouldReturnCreated() throws Exception {
        RewardCalculationResult calculationResult = new RewardCalculationResult(
                List.of(new RewardSummaryResponse("citizen-03", 510, BadgeLevel.GOLD)),
                List.of("warning"));
        when(rewardService.calculateRewards()).thenReturn(calculationResult);

        mockMvc.perform(post("/api/v1/rewards/calculate"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/rewards"))
                .andExpect(jsonPath("$.summaries[0].citizenId").value("citizen-03"))
                .andExpect(jsonPath("$.warnings[0]").value("warning"));
    }

    @Test
    void calculateRewardsShouldReturnServiceUnavailableWhenCrowdsourcedDown() throws Exception {
        when(rewardService.calculateRewards())
                .thenThrow(new CrowdsourcedServiceUnavailableException("Crowdsourced service is unavailable", new RuntimeException("down")));

        mockMvc.perform(post("/api/v1/rewards/calculate"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Crowdsourced service unavailable"));
    }
}

