package com.waterquality.crowdsourced.service;

import org.springframework.stereotype.Service;
import com.waterquality.crowdsourced.model.WaterQualitySubmission;

@Service
public class ValidationService {
    
    /**
     * Validates a water quality submission
     * Valid if: has postcode AND (at least one measurement OR at least one observation)
     */
    public boolean isValid(WaterQualitySubmission submission) {
        // Check postcode exists
        if (submission.getPostcode() == null || submission.getPostcode().trim().isEmpty()) {
            return false;
        }
        
        // Check if at least one measurement exists
        boolean hasMeasurement = submission.getTemperature() != null ||
                                 submission.getPh() != null ||
                                 submission.getAlkalinity() != null ||
                                 submission.getTurbidity() != null;
        
        // Check if observations exist
        boolean hasObservation = submission.getObservations() != null && 
                                !submission.getObservations().trim().isEmpty();
        
        // Valid if has postcode AND (measurement OR observation)
        return hasMeasurement || hasObservation;
    }
    
    /**
     * Checks if submission is complete (all fields filled)
     * Used for bonus points in Rewards service
     */
    public boolean isComplete(WaterQualitySubmission submission) {
        return submission.getPostcode() != null &&
               submission.getTemperature() != null &&
               submission.getPh() != null &&
               submission.getAlkalinity() != null &&
               submission.getTurbidity() != null &&
               submission.getObservations() != null &&
               !submission.getObservations().trim().isEmpty();
    }
}