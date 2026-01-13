package com.waterQualityMonitoring.crowdsourced.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.waterQualityMonitoring.crowdsourced.model.Observation;

/**
 * Spring Data repository for persisting {@link Observation} aggregates.
 */
@Repository
public interface ObservationRepository extends JpaRepository<Observation, UUID> {
}

