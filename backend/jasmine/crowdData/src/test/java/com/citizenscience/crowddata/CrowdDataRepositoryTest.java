package com.citizenscience.crowddata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.citizenscience.crowddata.model.CrowdData;
import com.citizenscience.crowddata.repository.CrowdDataRepository;

@DataJpaTest
public class CrowdDataRepositoryTest {
	
	@Autowired
	private CrowdDataRepository crowdDataRepository;
	
	@Test
	public void testGetAll() {
		String obs1 = "Cloudy";
		String obs2 = "Clear";
		boolean[] img1 = {true, false, false};
		boolean[] img2 = {false, false, false};
		
		
		CrowdData cd1 = new CrowdData((long)3,  LocalDateTime.now(), "SR3 2UT", (double)20, (double)9, (double)9, (double)40, obs1, img1, true);
		CrowdData cd2 = new CrowdData((long)2,  LocalDateTime.now(), "SR3 2UT", (double)16, (double)0, (double)0, (double)0, obs2, img2, true);
		crowdDataRepository.saveAll(List.of(cd1, cd2));
		
		List<CrowdData> lstCrowdData = crowdDataRepository.findAll();
		
		assertEquals(2, lstCrowdData.size());
		assertTrue(lstCrowdData.contains(cd1));
		assertTrue(lstCrowdData.contains(cd2));
	}
	
	@Test
	public void testGetById() {
		//arrange
		String obs3 = "Clear";
		boolean[] img3 = {true, false, false};
		CrowdData cd3 = new CrowdData((long)1,  LocalDateTime.now(), "SR3 2UT", (double)12, (double)4, (double)0, (double)2, obs3, img3, true);
		crowdDataRepository.save(cd3);
		
		CrowdData foundCrowdData = crowdDataRepository.getById(cd3.getDataID());
		
		assertEquals(cd3, foundCrowdData);
	}
}

