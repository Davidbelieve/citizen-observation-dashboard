package com.waterQualityMonitoring.reward.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.waterQualityMonitoring.reward.dto.CrowdsourcedObservation;
import com.waterQualityMonitoring.reward.service.exception.CrowdsourcedServiceUnavailableException;

/**
 * Fetches validated observations from the Crowdsourced microservice using
 * Spring's {@link RestClient}.
 */
@Component
public class RestCrowdsourcedObservationClient implements CrowdsourcedObservationClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestCrowdsourcedObservationClient.class);

    private final RestClient restClient;
    private final int pageSize;

    public RestCrowdsourcedObservationClient(
            RestClient.Builder restClientBuilder,
            @Value("${reward.crowdsourced.base-url:http://localhost:8080}") String baseUrl,
            @Value("${reward.crowdsourced.page-size:100}") int pageSize) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
        this.pageSize = pageSize;
    }

    @Override
    public List<CrowdsourcedObservation> fetchValidatedObservations() {
        List<CrowdsourcedObservation> results = new ArrayList<>();

        int page = 0;
        ObservationPage currentPage;

        try {
            do {
                currentPage = requestPage(page);
                if (currentPage == null || currentPage.getContent() == null) {
                    break;
                }
                results.addAll(
                        currentPage.getContent().stream()
                                .map(this::mapObservation)
                                .map(Optional::ofNullable)
                                .flatMap(Optional::stream)
                                .collect(Collectors.toList()));
                page++;
            } while (page < currentPage.getTotalPages());
        } catch (RestClientException ex) {
            LOGGER.warn("Failed to fetch observations from crowdsourced service: {}", ex.getMessage());
            throw new CrowdsourcedServiceUnavailableException("Crowdsourced service is unavailable", ex);
        }

        return results;
    }

    private ObservationPage requestPage(int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/observations")
                        .queryParam("page", page)
                        .queryParam("size", pageSize)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new CrowdsourcedServiceUnavailableException(
                            "Crowdsourced service returned status " + response.getStatusCode(), null);
                })
                .body(ObservationPage.class);
    }


    private CrowdsourcedObservation mapObservation(ObservationDto dto) {
        if (dto == null || dto.getId() == null) {
            return null;
        }

        CrowdsourcedObservation observation = new CrowdsourcedObservation();
        observation.setObservationId(dto.getId());
        String citizenId = dto.getCitizenUniqueId();
        if (citizenId == null || citizenId.isBlank()) {
            citizenId = dto.getCitizenId();
        }
        observation.setCitizenId(citizenId);
        observation.setPostcode(dto.getPostcode());
        observation.setSubmittedAt(dto.getSubmittedAt());
        observation.setValidated(Boolean.TRUE.equals(dto.getValidated()));
        observation.setTags(dto.getTags());

        if (dto.getMeasurement() != null) {
            CrowdsourcedObservation.Measurement measurement = new CrowdsourcedObservation.Measurement();
            measurement.setTemperatureC(dto.getMeasurement().getTemperatureC());
            measurement.setpH(dto.getMeasurement().getpH());
            measurement.setAlkalinityMgPerL(dto.getMeasurement().getAlkalinityMgPerL());
            measurement.setTurbidityNtu(dto.getMeasurement().getTurbidityNtu());
            observation.setMeasurement(measurement);
        }

        if (dto.getImages() != null) {
            List<String> filenames = dto.getImages().stream()
                    .map(image -> {
                        if (image == null) {
                            return null;
                        }
                        if (image.getFilePath() != null && !image.getFilePath().isBlank()) {
                            return image.getFilePath();
                        }
                        return image.getFilename();
                    })
                    .filter(path -> path != null && !path.isBlank())
                    .toList();
            observation.setImageFilenames(filenames);
        }

        return observation;
    }

    /**
     * Lightweight representation of the paginated response produced by the
     * crowdsourced service.
     */
    static final class ObservationPage {
        private List<ObservationDto> content;
        private int page;
        private int size;
        private int totalPages;

        public List<ObservationDto> getContent() {
            return content;
        }

        public void setContent(List<ObservationDto> content) {
            this.content = content;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }

    /**
     * Matches the JSON structure returned for each observation in the crowdsourced
     * service response.
     */
    static final class ObservationDto {
        private String id;
        private String citizenUniqueId;
        private String citizenId;
        private String postcode;
        private Instant submittedAt;
        private Boolean validated;
        private MeasurementDto measurement;
        private List<ImageDto> images;
        private List<String> tags;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCitizenUniqueId() {
            return citizenUniqueId;
        }

        public void setCitizenUniqueId(String citizenUniqueId) {
            this.citizenUniqueId = citizenUniqueId;
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

        public Boolean getValidated() {
            return validated;
        }

        public void setValidated(Boolean validated) {
            this.validated = validated;
        }

        public MeasurementDto getMeasurement() {
            return measurement;
        }

        public void setMeasurement(MeasurementDto measurement) {
            this.measurement = measurement;
        }

        public List<ImageDto> getImages() {
            return images;
        }

        public void setImages(List<ImageDto> images) {
            this.images = images;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    static final class MeasurementDto {
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
    }

    static final class ImageDto {
        private String filename;
        private String filePath;

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
    }
}

