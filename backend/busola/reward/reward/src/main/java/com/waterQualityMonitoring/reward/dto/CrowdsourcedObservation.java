package com.waterQualityMonitoring.reward.dto;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DTO representing the subset of observation data consumed by the rewards
 * service.
 * <p>
 * This structure mirrors the crowdsourced microservice response to decouple the
 * reward calculation logic from persistence concerns.
 * </p>
 */
public class CrowdsourcedObservation {

    private String observationId;
    private String citizenId;
    private String postcode;
    private Instant submittedAt;
    private boolean validated;
    private Measurement measurement;
    private List<String> imageFilenames;
    private List<String> tags;

    public String getObservationId() {
        return observationId;
    }

    public void setObservationId(String observationId) {
        this.observationId = observationId;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public List<String> getImageFilenames() {
        return imageFilenames == null ? Collections.emptyList() : imageFilenames;
    }

    public void setImageFilenames(List<String> imageFilenames) {
        this.imageFilenames = imageFilenames;
    }

    public List<String> getTags() {
        return tags == null ? Collections.emptyList() : tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Indicates whether the observation includes all measurement fields required
     * for the completeness bonus.
     *
     * @return {@code true} when every measurement value is provided
     */
    public boolean hasCompleteMeasurement() {
        if (measurement == null) {
            return false;
        }
        return measurement.hasAllFields();
    }

    /**
     * Checks whether textual observations are present and non-blank.
     */
    public boolean hasObservations() {
        if (!getTags().isEmpty()) {
            return getTags().stream().allMatch(tag -> tag != null && !tag.isBlank());
        }
        return false;
    }

    /**
     * Checks whether at least one image is provided with a filename.
     */
    public boolean hasImages() {
        return !getImageFilenames().isEmpty()
                && getImageFilenames().stream().allMatch(filename -> filename != null && !filename.isBlank());
    }

    public boolean hasTags() {
        return !getTags().isEmpty();
    }

    /**
     * Checks that the postcode field is populated.
     */
    public boolean hasPostcode() {
        return postcode != null && !postcode.isBlank();
    }

    @Override
    public int hashCode() {
        return Objects.hash(observationId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CrowdsourcedObservation)) {
            return false;
        }
        CrowdsourcedObservation other = (CrowdsourcedObservation) obj;
        return Objects.equals(observationId, other.observationId);
    }

    /**
     * Nested DTO representing measurement data within an observation.
     */
    public static class Measurement {
        private Double temperatureC;
        private Double pH;
        private Double alkalinityMgPerL;
        private Double turbidityNtu;

        public Double getTemperatureC() {
            return temperatureC;
        }

        public void setTemperatureC(Double temperatureC) {
            this.temperatureC = temperatureC;
        }

        public Double getpH() {
            return pH;
        }

        public void setpH(Double pH) {
            this.pH = pH;
        }

        public Double getAlkalinityMgPerL() {
            return alkalinityMgPerL;
        }

        public void setAlkalinityMgPerL(Double alkalinityMgPerL) {
            this.alkalinityMgPerL = alkalinityMgPerL;
        }

        public Double getTurbidityNtu() {
            return turbidityNtu;
        }

        public void setTurbidityNtu(Double turbidityNtu) {
            this.turbidityNtu = turbidityNtu;
        }

        private boolean hasAllFields() {
            return temperatureC != null
                    && pH != null
                    && alkalinityMgPerL != null
                    && turbidityNtu != null;
        }
    }
}

