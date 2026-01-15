package uk.northumbria.citizen.crowdsourced_data_ms.util;

import uk.northumbria.citizen.crowdsourced_data_ms.model.Observation;

public class ValidationUtil {
   
    public static boolean isValid(Observation observation) {
        if (observation == null || observation.getPostcode() == null || observation.getPostcode().trim().isEmpty()) {
            return false;
        }
        
        // Check if at least one measurement exists
        boolean hasMeasurement = observation.getTemperature() != null ||
                                observation.getPh() != null ||
                                observation.getAlkalinity() != null ||
                                observation.getTurbidity() != null;
        
        // Check if at least one observation note exists
        boolean hasObservation = observation.getObservations() != null &&
                                !observation.getObservations().isEmpty() &&
                                observation.getObservations().stream().anyMatch(note -> note != null && !note.trim().isEmpty());
        
        // Valid if postcode exists and (at least one measurement OR at least one observation)
        return hasMeasurement || hasObservation;
    }
}

