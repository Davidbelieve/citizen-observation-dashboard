package uk.northumbria.citizen.rewards_ms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the reward response sent to the client.
 * Contains the citizen ID, total points, and current badge.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {
    
    @JsonProperty("citizenId")
    private String citizenId;
    
    @JsonProperty("points")
    private Integer points;
    
    @JsonProperty("badge")
    private String badge;

    @JsonProperty("observationCount")
    private Integer observationCount;

    public RewardResponse(String citizenId, Integer points, String badge) {
        this.citizenId = citizenId;
        this.points = points;
        this.badge = badge;
        this.observationCount = 0;
    }
}
