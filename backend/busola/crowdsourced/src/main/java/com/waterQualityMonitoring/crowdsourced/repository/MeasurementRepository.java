package com.waterQualityMonitoring.crowdsourced.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.waterQualityMonitoring.crowdsourced.model.Measurement;

/**
 * Repository managing the lifecycle of {@link Measurement} entities.
 */
@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {
}
