package uk.northumbria.citizen.crowdsourced_data_ms.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.northumbria.citizen.crowdsourced_data_ms.dto.ObservationRequest;
import uk.northumbria.citizen.crowdsourced_data_ms.dto.ObservationResponse;
import uk.northumbria.citizen.crowdsourced_data_ms.model.Observation;
import uk.northumbria.citizen.crowdsourced_data_ms.repo.ObservationRepository;
import uk.northumbria.citizen.crowdsourced_data_ms.util.ValidationUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservationServiceTest {
    
    @Mock
    private ObservationRepository observationRepository;
    
    @InjectMocks
    private ObservationServiceImpl observationService;
    
    private ObservationRequest validRequest;
    private Observation savedObservation;
    
    @BeforeEach
    void setUp() {
        validRequest = new ObservationRequest();
        validRequest.setCitizenId("C200");
        validRequest.setPostcode("NE1 7ST");
        validRequest.setTemperature(26.3);
        validRequest.setPh(7.2);
        validRequest.setObservations(Arrays.asList("Clear water"));
        
        savedObservation = Observation.builder()
                .id(1L)
                .citizenId("C200")
                .postcode("NE1 7ST")
                .temperature(26.3)
                .ph(7.2)
                .observations(Arrays.asList("Clear water"))
                .valid(true)
                .build();
    }
    
    @Test
    void testCreateObservation_ValidRequest_ReturnsResponse() {
        // Arrange
        when(observationRepository.save(any(Observation.class))).thenReturn(savedObservation);
        
        // Act
        ObservationResponse response = observationService.createObservation(validRequest, null);
        
        // Assert
        assertNotNull(response);
        assertEquals("C200", response.getCitizenId());
        assertEquals("NE1 7ST", response.getPostcode());
        assertEquals(26.3, response.getTemperature());
        assertTrue(response.getValid());
        verify(observationRepository, times(1)).save(any(Observation.class));
    }
    
    @Test
    void testGetById_ExistingId_ReturnsResponse() {
        // Arrange
        when(observationRepository.findById(1L)).thenReturn(Optional.of(savedObservation));
        
        // Act
        ObservationResponse response = observationService.getById(1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("C200", response.getCitizenId());
        verify(observationRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetById_NonExistingId_ThrowsException() {
        // Arrange
        when(observationRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> observationService.getById(999L));
        verify(observationRepository, times(1)).findById(999L);
    }
    
    @Test
    void testGetAll_ReturnsAllObservations() {
        // Arrange
        List<Observation> observations = Arrays.asList(savedObservation);
        when(observationRepository.findAll()).thenReturn(observations);
        
        // Act
        List<ObservationResponse> responses = observationService.getAll();
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(observationRepository, times(1)).findAll();
    }
    
    @Test
    void testGetByCitizenId_ReturnsMatchingObservations() {
        // Arrange
        List<Observation> observations = Arrays.asList(savedObservation);
        when(observationRepository.findByCitizenIdIgnoreCase("C200")).thenReturn(observations);
        
        // Act
        List<ObservationResponse> responses = observationService.getByCitizenId("C200");
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("C200", responses.get(0).getCitizenId());
        verify(observationRepository, times(1)).findByCitizenIdIgnoreCase("C200");
    }
    
    @Test
    void testValidationUtil_ValidObservation_ReturnsTrue() {
        // Arrange
        Observation observation = Observation.builder()
                .postcode("NE1 7ST")
                .temperature(26.3)
                .build();
        
        // Act
        boolean isValid = ValidationUtil.isValid(observation);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void testValidationUtil_InvalidObservation_ReturnsFalse() {
        // Arrange
        Observation observation = Observation.builder()
                .postcode("")
                .build();
        
        // Act
        boolean isValid = ValidationUtil.isValid(observation);
        
        // Assert
        assertFalse(isValid);
    }
}

