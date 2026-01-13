package com.waterQualityMonitoring.crowdsourced.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;

/**
 * Composite key embedding observation and tag identifiers for the join entity.
 */
@Embeddable
public class ObservationObservationId implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6428218383158402587L;
	@Column(name = "observation_id")
    private UUID observationId;
    @Column(name = "observation_tag_id")
    private Integer observationTagId;

    public ObservationObservationId() {
    }
    public ObservationObservationId(UUID observationId, Integer observationTagId) {
        this.observationId = observationId;
        this.observationTagId = observationTagId;
    }
    public UUID getObservationId() {
        return observationId;
    }
    public void setObservationId(UUID observationId) {
        this.observationId = observationId;
    }
    public Integer getObservationTagId() {
        return observationTagId;
    }
    public void setObservationTagId(Integer observationTagId) {
        this.observationTagId = observationTagId;
    }
  // equals and hashCode
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ObservationObservationId that = (ObservationObservationId) o;
    return Objects.equals(observationId, that.observationId) && Objects.equals(observationTagId, that.observationTagId);
  }
  @Override
  public int hashCode() {
    return Objects.hash(observationId, observationTagId);
  }
}
