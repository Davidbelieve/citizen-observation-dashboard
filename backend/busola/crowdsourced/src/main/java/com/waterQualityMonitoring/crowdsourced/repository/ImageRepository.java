package com.waterQualityMonitoring.crowdsourced.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.waterQualityMonitoring.crowdsourced.model.Image;

/**
 * Repository for {@link Image} entities associated with observations.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
}
