package com.citizenscience.rewards;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger; import org.slf4j.LoggerFactory; 
import org.springframework.boot.CommandLineRunner; 
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration;

import com.citizenscience.rewards.model.Rewards;
import com.citizenscience.rewards.repository.RewardsRepository;
  
  @Configuration class LoadData {
  private static final Logger log = LoggerFactory.getLogger(LoadData.class);
  
  //sets some citizen records
  //rewards records aren't stored in the database and rewards cannot access a database
  //so they must be set this way
  @Bean CommandLineRunner initDatabase(RewardsRepository repository) {
	  return args -> { 
		 log.info("Preloading " + repository.save(new Rewards((long) 1, 90, "No Badge", true)));
		 log.info("Preloading " + repository.save(new Rewards((long) 2, 190, "Bronze", true)));
		 log.info("Preloading " + repository.save(new Rewards((long) 3, 150, "Bronze", true)));
		 log.info("Preloading " + repository.save(new Rewards((long) 4, 300, "Silver", true)));
		 log.info("Preloading " + repository.save(new Rewards((long) 5, 510, "Gold", true)));
	  };
  }
}