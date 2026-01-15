package uk.northumbria.citizen.rewards_ms.util;

import uk.northumbria.citizen.rewards_ms.dto.ObservationResponse;

import java.util.List;


public class RewardCalculator {
    
    private static final int POINTS_PER_VALID_SUBMISSION = 10;
    private static final int BONUS_POINTS_FOR_COMPLETE = 10;
    private static final int GOLD_THRESHOLD = 500;
    private static final int SILVER_THRESHOLD = 200;
    private static final int BRONZE_THRESHOLD = 100;
    
    
    public static int calculatePoints(List<ObservationResponse> observations) {
        if (observations == null || observations.isEmpty()) {
            return 0;
        }
        
        int totalPoints = 0;
        
        for (ObservationResponse observation : observations) {
            // Only count validated observations (valid = true)
            if (observation.getValid() != null && observation.getValid()) {
                // Base points for valid submission
                totalPoints += POINTS_PER_VALID_SUBMISSION;
                
                // Bonus points for complete submission
                if (isCompleteSubmission(observation)) {
                    totalPoints += BONUS_POINTS_FOR_COMPLETE;
                }
            }
        }
        
        return totalPoints;
    }
    
  
    private static boolean isCompleteSubmission(ObservationResponse observation) {
        // Check postcode
        if (observation.getPostcode() == null || observation.getPostcode().trim().isEmpty()) {
            return false;
        }
        
        // Check all 4 measurements (temperature, ph, alkalinity, turbidity)
        if (observation.getTemperature() == null ||
            observation.getPh() == null ||
            observation.getAlkalinity() == null ||
            observation.getTurbidity() == null) {
            return false;
        }
        
        // Check at least one observation text
        if (observation.getObservations() == null || 
            observation.getObservations().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    
    public static String determineBadge(int points) {
        if (points >= GOLD_THRESHOLD) {
            return "Gold";
        } else if (points >= SILVER_THRESHOLD) {
            return "Silver";
        } else if (points >= BRONZE_THRESHOLD) {
            return "Bronze";
        } else {
            return "None";
        }
    }
}

