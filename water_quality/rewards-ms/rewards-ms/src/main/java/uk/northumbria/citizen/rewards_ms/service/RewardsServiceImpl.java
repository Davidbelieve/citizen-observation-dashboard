package uk.northumbria.citizen.rewards_ms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import uk.northumbria.citizen.rewards_ms.dto.ObservationResponse;
import uk.northumbria.citizen.rewards_ms.dto.RewardResponse;
import uk.northumbria.citizen.rewards_ms.util.RewardCalculator;

import java.util.List;

/**
 * Implementation of RewardsService.
 * Uses WebClient to communicate with the crowdsourced-data-ms service
 * and calculates rewards based on observation data.
 */
@Service
public class RewardsServiceImpl implements RewardsService {
    
    private static final Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);
    
    private final WebClient webClient;
    
    @Value("${crowd.service.base-url}")
    private String crowdServiceBaseUrl;
    
    public RewardsServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    @Override
    public RewardResponse getRewardsByCitizenId(String citizenId) {
        try {
            logger.info("Fetching rewards for citizenId: {}", citizenId);
            
            // Fetch observations from crowdsourced-data-ms
            List<ObservationResponse> observations = fetchObservationsFromCrowdService(citizenId);
            
            logger.info("Retrieved {} observations for citizenId: {}", observations.size(), citizenId);
            
            // Log observation details for debugging
            for (ObservationResponse obs : observations) {
                logger.debug("Observation ID: {}, Valid: {}, CitizenId: {}", 
                    obs.getId(), obs.getValid(), obs.getCitizenId());
            }
            
            // Calculate points
            int totalPoints = RewardCalculator.calculatePoints(observations);
            logger.info("Calculated {} points for citizenId: {}", totalPoints, citizenId);
            
            // Determine badge
            String badge = RewardCalculator.determineBadge(totalPoints);
            
            // Build and return response
            return new RewardResponse(citizenId, totalPoints, badge);
            
        } catch (WebClientResponseException e) {
            logger.error("Error fetching observations from crowd service for citizenId {}: {}", 
                citizenId, e.getMessage(), e);
            throw new RuntimeException("Error fetching observations from crowd service: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Server error for citizenId {}: {}", citizenId, e.getMessage(), e);
            throw new RuntimeException("Server error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Fetches observations for a citizen from the crowdsourced-data-ms service.
     * 
     * @param citizenId The citizen ID
     * @return List of observations
     */
    private List<ObservationResponse> fetchObservationsFromCrowdService(String citizenId) {
        String url = crowdServiceBaseUrl + "/api/observations/citizen/" + citizenId;
        logger.debug("Fetching observations from URL: {}", url);
        
        ObservationResponse[] observationsArray = webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ObservationResponse[].class)
                .block();
        
        if (observationsArray == null) {
            logger.warn("No observations returned from crowd service for citizenId: {}", citizenId);
            return List.of();
        }
        
        logger.debug("Received {} observations from crowd service", observationsArray.length);
        return List.of(observationsArray);
    }
}

