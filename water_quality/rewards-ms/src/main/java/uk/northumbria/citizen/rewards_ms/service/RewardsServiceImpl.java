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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of RewardsService.
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
            String trimmedId = citizenId != null ? citizenId.trim() : "";
            logger.info("Fetching rewards for citizenId: {}", trimmedId);
            
            List<ObservationResponse> observations = fetchObservationsFromCrowdService(trimmedId);
            int totalPoints = RewardCalculator.calculatePoints(observations);
            String badge = RewardCalculator.determineBadge(totalPoints);
            
            RewardResponse response = new RewardResponse(trimmedId, totalPoints, badge);
            response.setObservationCount(observations.size());
            return response;
            
        } catch (WebClientResponseException e) {
            logger.error("Error fetching observations for citizenId {}: {}", citizenId, e.getMessage());
            throw new RuntimeException("Error fetching observations: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Server error for citizenId {}: {}", citizenId, e.getMessage());
            throw new RuntimeException("Server error: " + e.getMessage());
        }
    }

    @Override
    public List<RewardResponse> getLeaderboard() {
        try {
            String url = crowdServiceBaseUrl + "/api/observations";
            ObservationResponse[] allObservations = webClient.get().uri(url).retrieve().bodyToMono(ObservationResponse[].class).block();
            
            if (allObservations == null || allObservations.length == 0) return Collections.emptyList();

            Map<String, List<ObservationResponse>> groupedByCitizen = java.util.Arrays.stream(allObservations)
                    .filter(obs -> obs.getCitizenId() != null)
                    .collect(Collectors.groupingBy(obs -> obs.getCitizenId().trim()));

            return groupedByCitizen.entrySet().stream()
                    .map(entry -> {
                        int points = RewardCalculator.calculatePoints(entry.getValue());
                        RewardResponse r = new RewardResponse(entry.getKey(), points, RewardCalculator.determineBadge(points));
                        r.setObservationCount(entry.getValue().size());
                        return r;
                    })
                    .sorted((r1, r2) -> Integer.compare(r2.getPoints(), r1.getPoints()))
                    .limit(10)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    private List<ObservationResponse> fetchObservationsFromCrowdService(String citizenId) {
        String url = crowdServiceBaseUrl + "/api/observations/citizen/" + citizenId;
        ObservationResponse[] array = webClient.get().uri(url).retrieve().bodyToMono(ObservationResponse[].class).block();
        return array == null ? List.of() : java.util.Arrays.asList(array);
    }
}
