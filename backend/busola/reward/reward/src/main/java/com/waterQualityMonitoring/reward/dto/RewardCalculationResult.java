package com.waterQualityMonitoring.reward.dto;

import java.util.Collections;
import java.util.List;

/**
 * Response payload returned after running a reward calculation cycle.
 * Contains computed summaries and any warnings encountered.
 */
public class RewardCalculationResult {

    private final List<RewardSummaryResponse> summaries;
    private final List<String> warnings;

    /**
     * Creates a result payload.
     *
     * @param summaries computed reward summaries
     * @param warnings  optional warnings encountered during processing
     */
    public RewardCalculationResult(List<RewardSummaryResponse> summaries, List<String> warnings) {
        this.summaries = summaries == null ? Collections.emptyList() : summaries;
        this.warnings = warnings == null ? Collections.emptyList() : warnings;
    }

    public List<RewardSummaryResponse> getSummaries() {
        return summaries;
    }

    public List<String> getWarnings() {
        return warnings;
    }
}

