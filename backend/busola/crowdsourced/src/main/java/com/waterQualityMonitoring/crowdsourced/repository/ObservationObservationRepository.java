package com.waterQualityMonitoring.crowdsourced.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.waterQualityMonitoring.crowdsourced.model.ObservationObservation;
import com.waterQualityMonitoring.crowdsourced.model.ObservationObservationId;

/**
 * Repository for join entities linking observations with tags.
 */
@Repository
public interface ObservationObservationRepository extends JpaRepository<ObservationObservation, ObservationObservationId> {
}
