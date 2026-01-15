package uk.northumbria.citizen.rewards_ms.service;

import uk.northumbria.citizen.rewards_ms.dto.RewardResponse;

/**
 * Service interface for reward calculation operations.
 * Defines methods for retrieving and calculating citizen rewards.
 */
public interface RewardsService {
    
    /**
     * Retrieves rewards for a specific citizen by their ID.
     * 
     * @param citizenId The ID of the citizen
     * @return RewardResponse containing points and badge
     */
    RewardResponse getRewardsByCitizenId(String citizenId);

    /**
     * Retrieves the top contributors.
     * 
     * @return List of RewardResponse for top citizens
     */
    java.util.List<RewardResponse> getLeaderboard();
}

