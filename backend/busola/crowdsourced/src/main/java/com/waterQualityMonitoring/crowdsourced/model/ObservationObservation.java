package com.waterQualityMonitoring.crowdsourced.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import java.time.Instant;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
/**
 * Join entity linking {@link Observation} and {@link ObservationTag}.
 * Maintains timestamps and helper logic to keep both sides of the relationship
 * synchronized.
 */
@Entity
@Table(name = "observation_observation")
public class ObservationObservation {
    @EmbeddedId
    private ObservationObservationId id = new ObservationObservationId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("observationId")
    @JoinColumn(name = "observation_id", nullable = false)
    private Observation observation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("observationTagId")
    @JoinColumn(name = "observation_tag_id", nullable = false)
    private ObservationTag observationTag;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // getters and setters
    public ObservationObservationId getId() {
        return id;
    }
    public void setId(ObservationObservationId id) {
        this.id = id;
    }
    public Observation getObservation() {
        return observation;
    }
    public void setObservation(Observation observation) {
        this.observation = observation;
        this.id.setObservationId(observation != null ? observation.getId() : null);
    }
    public ObservationTag getObservationTag() {
        return observationTag;
    }
    public void setObservationTag(ObservationTag observationTag) {
        this.observationTag = observationTag;
        this.id.setObservationTagId(observationTag != null ? observationTag.getId() : null);
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
