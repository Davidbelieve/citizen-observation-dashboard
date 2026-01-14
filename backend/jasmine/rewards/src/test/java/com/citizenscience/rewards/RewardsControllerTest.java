package com.citizenscience.rewards;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.citizenscience.rewards.repository.RewardsRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class RewardsControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void testGetRewardsById() throws Exception{
		
		long id = 1;
		
		ResultActions result = mockMvc.perform(get("/citizenscience/rewards/{id}", id));
		
		result.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(id));
	}
	
	@Test
	public void testGetRewardsByIdError() throws Exception{
		long id = 100;
		ResultActions result = mockMvc.perform(get("/citizenscience/rewards/{id}", id));
		result.andExpect(status().isNotFound());
	}
	
	
}
