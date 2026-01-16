package com.waterquality.crowdsourced.repository;

import com.waterquality.crowdsourced.model.WaterQualitySubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<WaterQualitySubmission, UUID> {
    
    // Spring Data JPA automatically implements these methods!
    List<WaterQualitySubmission> findByCitizenId(String citizenId);
}