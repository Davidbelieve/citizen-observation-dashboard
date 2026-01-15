package com.waterquality.crowdsourced_data.repository;
//Necessary imports
import com.waterquality.crowdsourced_data.model.WaterQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface WaterQualityRepository  extends JpaRepository<WaterQuality,String>{
	//list of the observation for a specific citizen
	List<WaterQuality> findByCitizenId(String citizenId);
	
	//list of the observation for a specific postcode
	List<WaterQuality> findByPostcode(String postcode);
	
	//count the number of observations submitted by a specific citizen.
	long countByCitizenId(String citizenId);
	//find the highest citizen number for the current year
	@Query("SELECT MAX(CAST(SUBSTRING(w.citizenId, 5, LENGTH(w.citizenId))AS int)) "
			+ "FROM WaterQuality w WHERE w.citizenId LIKE :yearPrefix")
	Integer findMaxCitizenNumberForYear(@Param("yearPrefix") String yearPrefix);
}
