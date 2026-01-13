package com.waterQualityMonitoring.crowdsourced.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waterQualityMonitoring.crowdsourced.model.Crowdsourced;
import com.waterQualityMonitoring.crowdsourced.model.Observation;
import com.waterQualityMonitoring.crowdsourced.model.ObservationTagType;
import com.waterQualityMonitoring.crowdsourced.repository.ImageRepository;
import com.waterQualityMonitoring.crowdsourced.repository.MeasurementRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationObservationRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationTagRepository;

@SpringBootTest
@AutoConfigureMockMvc
class CrowdsourcedControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObservationRepository observationRepository;
    @Autowired
    private ObservationTagRepository observationTagRepository;
    @Autowired
    private ObservationObservationRepository observationObservationRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private MeasurementRepository measurementRepository;

    private String tagName;
    private String citizenUniqueId;

    @BeforeEach
    void setUp() {
        observationObservationRepository.deleteAll();
        imageRepository.deleteAll();
        measurementRepository.deleteAll();
        observationRepository.deleteAll();

        citizenUniqueId = "EXT-1";

        tagName = ObservationTagType.CLEAR.label();
    }

    @Test
    void shouldRejectInvalidPayload() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "postcode", "NE1 4ST",
                "citizenUniqueId", citizenUniqueId
        );

        mockMvc.perform(post("/api/v1/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        assertThat(observationRepository.count()).isZero();
    }

    @Test
    void shouldRejectWhenNoTagsAndNoMeasurement() throws Exception {
        Map<String, Object> requestBody = Map.of(
                "citizenUniqueId", citizenUniqueId,
                "postcode", "NE1 4ST",
                "validated", true
        );

        mockMvc.perform(post("/api/v1/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("At least one observation tag or measurement is required."));
    }

    @Test
    void shouldRejectInvalidPostcodeFormat() throws Exception {
        Crowdsourced.MeasurementPayload measurement = new Crowdsourced.MeasurementPayload();
        measurement.setTemperatureC(10.0);

        Map<String, Object> requestBody = Map.of(
                "citizenUniqueId", citizenUniqueId,
                "postcode", "INVALID",
                "validated", true,
                "measurement", measurement
        );

        mockMvc.perform(post("/api/v1/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid UK postcode format"));
    }

    @Test
    void shouldRejectInvalidTagValue() throws Exception {
        String allowedTags = ObservationTagType.labels().toString();

        Map<String, Object> requestBody = Map.of(
                "postcode", "NE1 4ST",
                "citizenUniqueId", citizenUniqueId,
                "validated", true,
                "tags", List.of("Unknown")
        );

        mockMvc.perform(post("/api/v1/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid observation tags: [Unknown]. Allowed values: " + allowedTags));
    }

    @Test
    void shouldCreateObservationWithMeasurementOnly() throws Exception {
        Crowdsourced.MeasurementPayload measurement = new Crowdsourced.MeasurementPayload();
        measurement.setTemperatureC(13.4);

        Map<String, Object> requestBody = Map.of(
                "postcode", "NE1 4ST",
                "citizenUniqueId", citizenUniqueId,
                "validated", true,
                "measurement", measurement
        );

        mockMvc.perform(post("/api/v1/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Observation created successfully"));
    }


}

