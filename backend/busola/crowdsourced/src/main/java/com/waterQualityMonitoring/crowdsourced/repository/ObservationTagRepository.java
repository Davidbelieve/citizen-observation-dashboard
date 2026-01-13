package com.waterQualityMonitoring.crowdsourced.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.waterQualityMonitoring.crowdsourced.model.ObservationTag;

/**
 * Repository providing access to {@link ObservationTag} lookup operations.
 */
@Repository
public interface ObservationTagRepository extends JpaRepository<ObservationTag, Integer> {

    /**
     * Finds a tag by its name, ignoring case.
     *
     * @param name case-insensitive tag name
     * @return optional containing the tag when present
     */
    Optional<ObservationTag> findByNameIgnoreCase(String name);
}

