package com.waterQualityMonitoring.reward.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.waterQualityMonitoring.reward.dto.CrowdsourcedObservation;
import com.waterQualityMonitoring.reward.dto.CrowdsourcedObservation.Measurement;

/**
 * Thread-safe in-memory implementation supplying sample observation data for
 * local development and testing.
 */
public class StubCrowdsourcedObservationClient implements CrowdsourcedObservationClient {

    private final List<CrowdsourcedObservation> observations = new CopyOnWriteArrayList<>();

    /**
     * Creates the client with a set of seed observations.
     */
    public StubCrowdsourcedObservationClient() {
        seedData();
    }

    @Override
    /**
     * Returns a defensive copy of the stored observations.
     */
    public List<CrowdsourcedObservation> fetchValidatedObservations() {
        return new ArrayList<>(observations);
    }

    /**
     * Replaces the underlying sample data set. Primarily used during tests.
     *
     * @param newObservations replacement observations
     */
    public void replaceObservations(List<CrowdsourcedObservation> newObservations) {
        observations.clear();
        if (newObservations != null) {
            observations.addAll(newObservations);
        }
    }

    /**
     * Populates the client with representative observations that illustrate
     * complete and partial submissions.
     */
    private void seedData() {
        CrowdsourcedObservation completeObservation = new CrowdsourcedObservation();
        completeObservation.setObservationId("seed-1");
        completeObservation.setCitizenId("citizen-001");
        completeObservation.setPostcode("NE1 1AA");
        completeObservation.setValidated(true);
        completeObservation.setSubmittedAt(Instant.now());
        Measurement measurement = new Measurement();
        measurement.setTemperatureC(14.2);
        measurement.setpH(7.3);
        measurement.setAlkalinityMgPerL(120.0);
        measurement.setTurbidityNtu(1.2);
        completeObservation.setMeasurement(measurement);
        completeObservation.setImageFilenames(List.of("image1.jpg"));
        completeObservation.setTags(List.of("Clear", "Presence of Odour"));

        CrowdsourcedObservation partialObservation = new CrowdsourcedObservation();
        partialObservation.setObservationId("seed-2");
        partialObservation.setCitizenId("citizen-002");
        partialObservation.setPostcode("NE2 2BB");
        partialObservation.setValidated(true);
        partialObservation.setSubmittedAt(Instant.now());
        partialObservation.setTags(List.of("Discoloured"));

        observations.add(completeObservation);
        observations.add(partialObservation);
    }
}

