package com.citizenscience.rewards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.citizenscience.rewards.model.Rewards;
import com.citizenscience.rewards.repository.RewardsRepository;

@DataJpaTest
public class RewardsRepositoryTest {
	
	@Autowired
	private RewardsRepository rewardsRepository;
	
	@Test
	public void testGetAll() {
		
		Rewards r2 = new Rewards((long) 2, 190, "Bronze", true);
		Rewards r1 = new Rewards((long) 1, 90, "No Badge", true);
		rewardsRepository.saveAll(List.of(r1, r2));
		
		List<Rewards> lstRewards = rewardsRepository.findAll();
		
		assertEquals(2, lstRewards.size());
		assertTrue(lstRewards.contains(r1));
		assertTrue(lstRewards.contains(r2));
	}
	
	@Test
	public void testGetById() {
		Rewards r3 = new Rewards((long) 3, 489, "Silver", true);
		rewardsRepository.save(r3);
		
		Rewards foundRewards = rewardsRepository.getById(r3.getId());
		
		assertEquals(r3, foundRewards);
	}
}

