package com.waterQualityMonitoring.reward.dto;

import com.waterQualityMonitoring.reward.model.BadgeLevel;

/**
 * DTO summarising the reward status for a single citizen.
 */
public class RewardSummaryResponse {

    private String citizenId;
    private int totalPoints;
    private String badge;

    public RewardSummaryResponse() {
    }

    /**
     * Creates a summary response.
     *
     * @param citizenId  unique citizen identifier
     * @param totalPoints points accumulated so far
     * @param badgeLevel  badge earned corresponding to the points
     */
    public RewardSummaryResponse(String citizenId, int totalPoints, BadgeLevel badgeLevel) {
        this.citizenId = citizenId;
        this.totalPoints = totalPoints;
        this.badge = badgeLevel.name();
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }
}

