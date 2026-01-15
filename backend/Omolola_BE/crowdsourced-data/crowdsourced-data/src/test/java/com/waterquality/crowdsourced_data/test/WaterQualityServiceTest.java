package com.waterquality.crowdsourced_data.test;

//Necessary imports
import com.waterquality.crowdsourced_data.model.WaterQuality;
import com.waterquality.crowdsourced_data.repository.WaterQualityRepository;
import com.waterquality.crowdsourced_data.service.WaterQualityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WaterQualityServiceTest {

	@Mock
	private WaterQualityRepository repository;
	
	@InjectMocks
	private WaterQualityService service;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}
	
	//Valid observation submission should succeed
	@Test
	public void testSubmitValidObservation_Success() {
		//create a valid observation
		WaterQuality observation = new WaterQuality("NE8 1SS");
		observation.setpH(7.2);
		observation.setObservations("CLEAR");
		
		//return observation when saved
		when(repository.save(any(WaterQuality.class))).thenReturn(observation);
		when(repository.findMaxCitizenNumberForYear(anyString())).thenReturn(null);
		
		//submit the observation
		WaterQuality result = service.submitObservation(observation);
		
		//verify save
		assertNotNull(result);
		assertEquals("NE8 1SS", result.getPostcode());
		verify(repository, times(1)).save(any(WaterQuality.class));
		
	}
	
	
	//Invalid observation(no postcode/ no measurement) should fail
	@Test
	public void testSubmitInvalidObservation_ThrowsException() {
		// create an invalid observation(no postcode/ no measurement)
		WaterQuality observation = new WaterQuality();
		//should throw IllegalArgumentException
		Exception exception = assertThrows(IllegalArgumentException.class, () ->{
			service.submitObservation(observation);
		});
		
		//verify error messgae contains expected text
		String expectedMessage = "Invalid observation";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
		
		//verify save was not called
		verify(repository, never()).save(any(WaterQuality.class));
		
	}
	
	
}
