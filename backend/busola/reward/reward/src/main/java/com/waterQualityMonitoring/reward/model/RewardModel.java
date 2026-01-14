package com.waterQualityMonitoring.reward.model;

import java.util.Objects;

/**
 * Immutable value object capturing the reward state for a citizen.
 */
public class RewardModel {

    private final String citizenId;
    private final int totalPoints;
    private final BadgeLevel badgeLevel;

    /**
     * Constructs a reward model value object.
     *
     * @param citizenId   unique citizen identifier
     * @param totalPoints accumulated points
     * @param badgeLevel  badge derived from the points
     */
    public RewardModel(String citizenId, int totalPoints, BadgeLevel badgeLevel) {
        this.citizenId = citizenId;
        this.totalPoints = totalPoints;
        this.badgeLevel = badgeLevel;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public BadgeLevel getBadgeLevel() {
        return badgeLevel;
    }

    /**
     * Returns a new value object with updated totals and derived badge level.
     *
     * @param additionalPoints points to add to the current total
     * @return new reward snapshot reflecting the updated totals
     */
    public RewardModel withUpdatedTotals(int additionalPoints) {
        int updatedPoints = this.totalPoints + additionalPoints;
        BadgeLevel updatedBadge = BadgeLevel.fromPoints(updatedPoints);
        return new RewardModel(this.citizenId, updatedPoints, updatedBadge);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RewardModel)) {
            return false;
        }
        RewardModel that = (RewardModel) o;
        return totalPoints == that.totalPoints
                && Objects.equals(citizenId, that.citizenId)
                && badgeLevel == that.badgeLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(citizenId, totalPoints, badgeLevel);
    }

    @Override
    public String toString() {
        return "RewardModel{" +
                "citizenId='" + citizenId + '\'' +
                ", totalPoints=" + totalPoints +
                ", badgeLevel=" + badgeLevel +
                '}';
    }
}
