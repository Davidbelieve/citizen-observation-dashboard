package com.citizenscience.rewards.repository;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.citizenscience.rewards.model.Rewards;

//makes a list of all the customer items
//which is what the controller uses to do all its commands
@Repository
public interface RewardsRepository extends JpaRepository<Rewards, Long> {

	List<Rewards> findByIsSet(boolean b);

	List<Rewards> findByOrderByPointsAsc(Limit of);

}