package com.waterquality.rewards.repository;

//Necessary Imports
import com.waterquality.rewards.model.Rewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;



@Repository
public interface RewardsRepository extends JpaRepository<Rewards, String> {
	
	//find reward record by citizenId
	Optional <Rewards> findByCitizenId(String citizenID);
	
	//get leaderboard- top citizens by points
	List<Rewards> findAllByOrderByTotalPointsDesc();
	
	//find all citizen with total points >= threshold
	@Query("Select r From Rewards r WHERE r.totalPoints >= :threshold ORDER BY r.totalPoints DESC")
	List<Rewards> findByPointsGreaterThanEqual(@Param("threshold") int threshold);
}
