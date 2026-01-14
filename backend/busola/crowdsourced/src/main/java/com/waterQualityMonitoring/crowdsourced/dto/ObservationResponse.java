package com.waterQualityMonitoring.crowdsourced.dto;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.waterQualityMonitoring.crowdsourced.model.Image;
import com.waterQualityMonitoring.crowdsourced.model.Measurement;
import com.waterQualityMonitoring.crowdsourced.model.Observation;
import com.waterQualityMonitoring.crowdsourced.model.ObservationObservation;

/**
 * API projection of the {@link Observation} aggregate.
 * <p>
 * Converts the entity graph into a flattened, read-friendly DTO that is shared
 * with clients and other microservices.
 * </p>
 */
public class ObservationResponse {

    private UUID id;
    private String postcode;
    private String citizenUniqueId;
    private Instant submittedAt;
    private Boolean validated;
    private String notes;
    private MeasurementDto measurement;
    private List<ImageDto> images;
    private List<String> tags;

    /**
     * Maps an {@link Observation} entity to an {@link ObservationResponse}.
     *
     * @param observation JPA entity loaded from the database
     * @return DTO representation of the observation
     */
    public static ObservationResponse fromEntity(Observation observation) {
        ObservationResponse response = new ObservationResponse();
        response.id = observation.getId();
        response.postcode = observation.getPostcode();
        response.citizenUniqueId = observation.getCitizenUniqueId();
        response.submittedAt = observation.getSubmittedAt();
        response.validated = observation.getValidated();
        response.notes = observation.getNotes();

        response.measurement = createMeasurementDto(observation.getMeasurement());

        response.images = observation.getImages() == null
                ? Collections.emptyList()
                : observation.getImages().stream()
                        .map(ObservationResponse::createImageDto)
                        .collect(Collectors.toList());

        response.tags = observation.getTagLinks() == null
                ? Collections.emptyList()
                : observation.getTagLinks().stream()
                        .map(ObservationObservation::getObservationTag)
                        .filter(tag -> tag != null)
                        .map(tag -> tag.getName())
                        .collect(Collectors.toList());

        return response;
    }

    private static MeasurementDto createMeasurementDto(Measurement measurement) {
        if (measurement == null) {
            return null;
        }
        MeasurementDto dto = new MeasurementDto();
        dto.temperatureC = measurement.getTemperatureC();
        dto.pH = measurement.getpH();
        dto.alkalinityMgPerL = measurement.getAlkalinityMgPerL();
        dto.turbidityNtu = measurement.getTurbidityNtu();
        return dto;
    }

    private static ImageDto createImageDto(Image image) {
        ImageDto dto = new ImageDto();
        dto.id = image.getId();
        dto.filename = image.getFilename();
        dto.filePath = image.getFilePath();
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCitizenUniqueId() {
        return citizenUniqueId;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Boolean getValidated() {
        return validated;
    }

    public String getNotes() {
        return notes;
    }

    public MeasurementDto getMeasurement() {
        return measurement;
    }

    public List<ImageDto> getImages() {
        return images;
    }

    public List<String> getTags() {
        return tags;
    }

    /**
     * DTO that captures the numeric measurements recorded for an observation.
     */
    public static class MeasurementDto {
        private Double temperatureC;
        private Double pH;
        private Double alkalinityMgPerL;
        private Double turbidityNtu;

        public Double getTemperatureC() {
            return temperatureC;
        }

        public Double getpH() {
            return pH;
        }

        public Double getAlkalinityMgPerL() {
            return alkalinityMgPerL;
        }

        public Double getTurbidityNtu() {
            return turbidityNtu;
        }
    }

    /**
     * DTO with metadata describing an image attached to an observation.
     */
    public static class ImageDto {
        private UUID id;
        private String filename;
        private String filePath;

        public UUID getId() {
            return id;
        }

        public String getFilename() {
            return filename;
        }

        public String getFilePath() {
            return filePath;
        }

    }
}

