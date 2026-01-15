package uk.northumbria.citizen.rewards_ms.util;

import uk.northumbria.citizen.rewards_ms.dto.ObservationResponse;
import java.util.List;

public class RewardCalculator {
    
    private static final int POINTS_PER_SUBMISSION = 10;
    private static final int BONUS_POINTS = 10;
    
    public static int calculatePoints(List<ObservationResponse> observations) {
        if (observations == null || observations.isEmpty()) {
            return 0;
        }
        
        int totalPoints = 0;
        for (ObservationResponse observation : observations) {
            // Give 10 points for every submission found
            totalPoints += POINTS_PER_SUBMISSION;
            
            // Give bonus points if it's high quality (has measurements)
            if (isHighQuality(observation)) {
                totalPoints += BONUS_POINTS;
            }
        }
        
        return totalPoints;
    }
    
    private static boolean isHighQuality(ObservationResponse observation) {
        return observation.getTemperature() != null || 
               observation.getPh() != null || 
               (observation.getObservations() != null && !observation.getObservations().isEmpty());
    }
    
    public static String determineBadge(int points) {
        if (points >= 500) return "Gold";
        if (points >= 200) return "Silver";
        if (points >= 100) return "Bronze";
        return "None";
    }
}
