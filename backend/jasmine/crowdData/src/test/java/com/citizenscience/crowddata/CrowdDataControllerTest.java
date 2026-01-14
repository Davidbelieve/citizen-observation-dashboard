package com.citizenscience.crowddata;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.citizenscience.crowddata.model.CrowdData;

@SpringBootTest
@AutoConfigureMockMvc
public class CrowdDataControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void testGetCSDById() throws Exception {
		
		long csdId = 1;
		
		ResultActions result = mockMvc.perform(get("/citizenscience/crowd/{id}", csdId));
		
		result.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.dataID").value(csdId));
	}
	
	@Test
	public void testGetRewardsByIdError() throws Exception{
		long id = 100;
		ResultActions result = mockMvc.perform(get("/citizenscience/crowd/{id}", id));
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void testCreateCD() throws Exception {
		
		String[] obs1 = {"Cloudy"};
		boolean[] img1 = {true, false, false};
		
		CrowdData mockCD = new CrowdData((long)4,  LocalDateTime.now(), "AB1 2CD", (double)17, (double)9, (double)9, (double)40, "Cloudy", img1, true);
		String requestBody = objectMapper.writeValueAsString(mockCD);
		
		ResultActions result = mockMvc.perform(post("/citizenscience/crowddata")
							.contentType(MediaType.APPLICATION_JSON)
							.content(requestBody));
		
		result.andExpect(status().isCreated());
	}
	
	@Test
	public void testCreateCDInvalid() throws Exception {
		
		String[] obs2 = {""};
		boolean[] img2 = {false, false, false};
		
		CrowdData mockCD = new CrowdData((long)4,  LocalDateTime.now(), "AB1 2CD", (double)0, (double)0, (double)0, (double)-0, "", img2, true);
		String requestBody = objectMapper.writeValueAsString(mockCD);
		
		ResultActions result = mockMvc.perform(post("/citizenscience/crowddata")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody));
		result.andExpect(status().isNotAcceptable());
	} 
	

}

