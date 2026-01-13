package com.waterQualityMonitoring.crowdsourced.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;

import com.waterQualityMonitoring.crowdsourced.model.validation.ValidObservationTags;
import com.waterQualityMonitoring.crowdsourced.model.validation.ValidUkPostcode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Size;

/**
 * Request model representing crowdsourced submissions received through the API.
 * 
 * Validation annotations ensure that core fields, measurements and supporting
 * metadata comply with business rules prior to persistence.
 * 
 */
public class Crowdsourced {
    
    @NotBlank
    @ValidUkPostcode
    private String postcode;

    @NotBlank
    private String citizenUniqueId;

    private Instant submittedAt;
    private Boolean validated;
    private String notes;

    @Valid
    private MeasurementPayload measurement;

    @Size(max = 3)
    private List<ImagePayload> images;

    @ValidObservationTags
    private List<String> tags;
    

    // getters and setters
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
    public Boolean getValidated() {
        return validated;
    }
    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getCitizenUniqueId() {
        return citizenUniqueId;
    }
    public void setCitizenUniqueId(String citizenUniqueId) {
        this.citizenUniqueId = citizenUniqueId;
    }

    @Valid
    public MeasurementPayload getMeasurement() {
        return measurement;
    }
    public void setMeasurement(MeasurementPayload measurement) {
        this.measurement = measurement;
    }
    public List<ImagePayload> getImages() {
        return images;
    }
    public void setImages(List<ImagePayload> images) {
        this.images = images;
    }
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    @AssertTrue(message = "Measurement must include at least one value when provided.")
    public boolean isMeasurementPayloadValid() {
        return measurement == null || hasMeasurementValues();
    }

    @JsonIgnore
    @AssertTrue(message = "At least one observation tag or measurement is required.")
    public boolean isObservationOrMeasurementProvided() {
        boolean hasTags = tags != null && !tags.isEmpty();
        return hasTags || hasMeasurementValues();
    }

    private boolean hasMeasurementValues() {
        if (measurement == null) {
            return false;
        }
        return measurement.getTemperatureC() != null
                || measurement.getpH() != null
                || measurement.getAlkalinityMgPerL() != null
                || measurement.getTurbidityNtu() != null;
    }

    /**
     * Nested payload describing optional measurement values captured during
     * sampling.
     */
    public static class MeasurementPayload {
        private Double temperatureC;
        private Double pH;
        private Double alkalinityMgPerL;
        private Double turbidityNtu;

        // getters and setters

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
    }

    /**
     * Nested payload describing image metadata accompanying an observation.
     */
    public static class ImagePayload {
        @NotBlank
        private String filename;
        @NotBlank
        private String filePath;
        @NotNull
        @Positive
        @jakarta.validation.constraints.Max(2_097_152)
        private Long fileSizeBytes;
        
        // getters and setters
        public String getFilename() {
            return filename;
        }
        public void setFilename(String filename) {
            this.filename = filename;
        }
        public String getFilePath() {
            return filePath;
        }
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
        public Long getFileSizeBytes() {
            return fileSizeBytes;
        }
        public void setFileSizeBytes(Long fileSizeBytes) {
            this.fileSizeBytes = fileSizeBytes;
        }
    }
}
