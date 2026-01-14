package com.waterQualityMonitoring.crowdsourced.testsupport;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.waterQualityMonitoring.crowdsourced.model.Crowdsourced;
import com.waterQualityMonitoring.crowdsourced.model.Image;
import com.waterQualityMonitoring.crowdsourced.model.Measurement;
import com.waterQualityMonitoring.crowdsourced.model.Observation;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Observation newObservation(String citizenUniqueId) {
        Observation observation = new Observation();
        observation.setCitizenUniqueId(citizenUniqueId);
        observation.setPostcode("NE1 4ST");
        observation.setNotes("Observation notes");
        observation.setValidated(Boolean.TRUE);
        observation.setSubmittedAt(Instant.now());
        return observation;
    }

    public static Image newImage(Observation observation) {
        Image image = new Image();
        image.setObservation(observation);
        image.setFilename("image-" + UUID.randomUUID() + ".jpg");
        image.setFilePath("/tmp/" + image.getFilename());
        return image;
    }

    public static Measurement newMeasurement(Observation observation) {
        Measurement measurement = new Measurement();
        measurement.setObservation(observation);
        measurement.setTemperatureC(12.3);
        measurement.setpH(7.2);
        measurement.setAlkalinityMgPerL(25.0);
        measurement.setTurbidityNtu(0.9);
        return measurement;
    }

    public static Crowdsourced newCrowdsourcedRequest(String citizenUniqueId) {
        Crowdsourced request = new Crowdsourced();
        request.setCitizenUniqueId(citizenUniqueId);
        request.setPostcode("NE1 4ST");
        request.setNotes("Water looks clear");
        request.setValidated(Boolean.TRUE);
        request.setSubmittedAt(Instant.now());
        request.setTags(List.of("Clear"));
        return request;
    }

    public static Crowdsourced newCrowdsourcedRequestWithMeasurement(String citizenUniqueId) {
        Crowdsourced request = newCrowdsourcedRequest(citizenUniqueId);

        Crowdsourced.MeasurementPayload measurement = new Crowdsourced.MeasurementPayload();
        measurement.setTemperatureC(13.4);
        measurement.setpH(7.3);
        measurement.setAlkalinityMgPerL(28.0);
        measurement.setTurbidityNtu(1.2);
        request.setMeasurement(measurement);

        Crowdsourced.ImagePayload imagePayload = new Crowdsourced.ImagePayload();
        imagePayload.setFilename("sample.jpg");
        imagePayload.setFilePath("/tmp/sample.jpg");
        imagePayload.setFileSizeBytes(1024L);
        request.setImages(List.of(imagePayload));

        return request;
    }
}

