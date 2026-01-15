package com.waterQualityMonitoring.crowdsourced.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.waterQualityMonitoring.crowdsourced.model.Observation;

/**
 * Spring Data repository for persisting {@link Observation} aggregates.
 */
@Repository
public interface ObservationRepository extends JpaRepository<Observation, UUID> {
    @Query(value = "SELECT MAX(CAST(SUBSTR(citizen_unique_id, 5) AS INTEGER)) "
            + "FROM observations WHERE citizen_unique_id LIKE :yearPrefix", nativeQuery = true)
    Integer findMaxCitizenNumberForYear(@Param("yearPrefix") String yearPrefix);
}

