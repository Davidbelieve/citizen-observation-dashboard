package uk.northumbria.citizen.rewards_ms.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import uk.northumbria.citizen.rewards_ms.dto.ObservationResponse;
import uk.northumbria.citizen.rewards_ms.dto.RewardResponse;
import uk.northumbria.citizen.rewards_ms.util.RewardCalculator;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RewardsService.
 * Tests the reward calculation logic for complete and incomplete submissions.
 */
@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {
    
    @Mock
    private WebClient webClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;
    
    @InjectMocks
    private RewardsServiceImpl rewardsService;
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rewardsService, "crowdServiceBaseUrl", "http://localhost:8081");
    }
    
    @Test
    void testCalculatePoints_CompleteSubmission() {
        // Test that a complete submission gives 20 points (10 base + 10 bonus)
        ObservationResponse completeObservation = new ObservationResponse();
        completeObservation.setId(1L);
        completeObservation.setCitizenId("C200");
        completeObservation.setPostcode("NE1 1AA");
        completeObservation.setTemperature(15.5);
        completeObservation.setPh(7.0);
        completeObservation.setAlkalinity(8.5);
        completeObservation.setTurbidity(10.0);
        completeObservation.setObservations(Arrays.asList("Water looks clear"));
        completeObservation.setValid(true);
        
        List<ObservationResponse> observations = Arrays.asList(completeObservation);
        int points = RewardCalculator.calculatePoints(observations);
        
        assertEquals(20, points, "Complete submission should give 20 points (10 base + 10 bonus)");
    }
    
    @Test
    void testCalculatePoints_IncompleteSubmission() {
        // Test that an incomplete submission gives only 10 points
        ObservationResponse incompleteObservation = new ObservationResponse();
        incompleteObservation.setId(2L);
        incompleteObservation.setCitizenId("C200");
        incompleteObservation.setPostcode("NE1 1AA");
        incompleteObservation.setTemperature(15.5);
        incompleteObservation.setPh(7.0);
        incompleteObservation.setAlkalinity(null); // Missing measurement
        incompleteObservation.setTurbidity(10.0);
        incompleteObservation.setObservations(Arrays.asList("Water looks clear"));
        incompleteObservation.setValid(true);
        
        List<ObservationResponse> observations = Arrays.asList(incompleteObservation);
        int points = RewardCalculator.calculatePoints(observations);
        
        assertEquals(10, points, "Incomplete submission should give only 10 points");
    }
    
    @Test
    void testDetermineBadge_Gold() {
        assertEquals("Gold", RewardCalculator.determineBadge(500));
        assertEquals("Gold", RewardCalculator.determineBadge(1000));
    }
    
    @Test
    void testDetermineBadge_Silver() {
        assertEquals("Silver", RewardCalculator.determineBadge(200));
        assertEquals("Silver", RewardCalculator.determineBadge(499));
    }
    
    @Test
    void testDetermineBadge_Bronze() {
        assertEquals("Bronze", RewardCalculator.determineBadge(100));
        assertEquals("Bronze", RewardCalculator.determineBadge(199));
    }
    
    @Test
    void testDetermineBadge_None() {
        assertEquals("None", RewardCalculator.determineBadge(0));
        assertEquals("None", RewardCalculator.determineBadge(99));
    }
    
    @Test
    void testCalculatePoints_EmptyList() {
        List<ObservationResponse> observations = List.of();
        int points = RewardCalculator.calculatePoints(observations);
        assertEquals(0, points);
    }
    
    @Test
    void testCalculatePoints_NullList() {
        int points = RewardCalculator.calculatePoints(null);
        assertEquals(0, points);
    }
    
    @Test
    void testCalculatePoints_UnvalidatedObservation() {
        // Unvalidated observations should not count
        ObservationResponse unvalidatedObservation = new ObservationResponse();
        unvalidatedObservation.setId(3L);
        unvalidatedObservation.setCitizenId("C200");
        unvalidatedObservation.setValid(false);
        
        List<ObservationResponse> observations = Arrays.asList(unvalidatedObservation);
        int points = RewardCalculator.calculatePoints(observations);
        
        assertEquals(0, points, "Unvalidated observations should not count");
    }
}

