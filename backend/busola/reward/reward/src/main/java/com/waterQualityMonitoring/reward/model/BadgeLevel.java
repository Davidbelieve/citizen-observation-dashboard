package com.waterQualityMonitoring.reward.model;

/**
 * Enumeration describing badges awarded based on cumulative points.
 */
public enum BadgeLevel {
    NONE(0),
    BRONZE(100),
    SILVER(200),
    GOLD(500);

    private final int threshold;

    BadgeLevel(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    /**
     * Resolves the badge level that matches the supplied points.
     *
     * @param points cumulative points earned by a citizen
     * @return badge level associated with the points
     */
    public static BadgeLevel fromPoints(int points) {
        if (points >= GOLD.threshold) {
            return GOLD;
        }
        if (points >= SILVER.threshold) {
            return SILVER;
        }
        if (points >= BRONZE.threshold) {
            return BRONZE;
        }
        return NONE;
    }
}

