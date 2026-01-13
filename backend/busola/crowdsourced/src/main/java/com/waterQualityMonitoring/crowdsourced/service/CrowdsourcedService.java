package com.waterQualityMonitoring.crowdsourced.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waterQualityMonitoring.crowdsourced.model.Crowdsourced;
import com.waterQualityMonitoring.crowdsourced.model.Crowdsourced.ImagePayload;
import com.waterQualityMonitoring.crowdsourced.model.Crowdsourced.MeasurementPayload;
import com.waterQualityMonitoring.crowdsourced.model.Image;
import com.waterQualityMonitoring.crowdsourced.model.Measurement;
import com.waterQualityMonitoring.crowdsourced.model.Observation;
import com.waterQualityMonitoring.crowdsourced.model.ObservationObservation;
import com.waterQualityMonitoring.crowdsourced.model.ObservationTag;
import com.waterQualityMonitoring.crowdsourced.model.ObservationTagType;
import com.waterQualityMonitoring.crowdsourced.repository.ImageRepository;
import com.waterQualityMonitoring.crowdsourced.repository.MeasurementRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationObservationRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationTagRepository;

import jakarta.persistence.EntityNotFoundException;

/**
 * Domain service orchestrating persistence and update workflows for
 * observations and their related entities.
 */
@Service
@Transactional
public class CrowdsourcedService {

    private final ObservationRepository observationRepository;
    private final ObservationTagRepository observationTagRepository;
    private final ObservationObservationRepository tagLinkRepository;
    private final ImageRepository imageRepository;
    private final MeasurementRepository measurementRepository;

    /**
     * Creates the service with its repository collaborators.
     *
     * @param observationRepository      repository for observation aggregates
     * @param observationTagRepository   repository for tag catalog records
     * @param tagLinkRepository          repository for observation-tag join entities
     * @param imageRepository            repository for observation images
     * @param measurementRepository      repository for measurement entities
     */
    public CrowdsourcedService(ObservationRepository observationRepository, ObservationTagRepository observationTagRepository, ObservationObservationRepository tagLinkRepository, ImageRepository imageRepository, MeasurementRepository measurementRepository) {
        this.observationRepository = observationRepository;
        this.observationTagRepository = observationTagRepository;
        this.tagLinkRepository = tagLinkRepository;
        this.imageRepository = imageRepository;
        this.measurementRepository = measurementRepository;
    }
    
    /**
     * Persists a new observation along with any nested measurement, image or tag
     * data supplied in the request.
     *
     * @param request validated submission
     * @return the fully populated persisted observation
     */
    public Observation createObservation(Crowdsourced request) {
        Observation observation = buildObservationFromRequest(request);
        Observation saved = observationRepository.save(observation);

        persistMeasurementIfPresent(request.getMeasurement(), saved);
        persistImagesIfPresent(request.getImages(), saved);
        persistTags(request.getTags(), saved);

        return reloadObservation(saved.getId());
    }

    @Transactional(readOnly = true)
    /**
     * Retrieves all observations without pagination.
     *
     * @return list of persisted observations
     */
    public List<Observation> getAllObservations() {
        return observationRepository.findAll();
    }

    @Transactional(readOnly = true)
    /**
     * Retrieves observations using Spring Data pagination.
     *
     * @param pageable pagination configuration
     * @return page containing observation entities
     */
    public Page<Observation> getObservations(Pageable pageable) {
        return observationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    /**
     * Looks up a single observation by identifier.
     *
     * @param id observation identifier
     * @return optional containing the entity if found
     */
    public Optional<Observation> getObservation(UUID id) {
        return observationRepository.findById(id);
    }

    /**
     * Updates an existing observation and synchronises dependent entities.
     *
     * @param id      identifier of the observation to update
     * @param request desired state of the observation
     * @return optional containing the updated observation when present
     */
    public Optional<Observation> updateObservation(UUID id, Crowdsourced request) {
        return observationRepository.findById(id).map(existing -> {
            applyObservationUpdates(existing, request);
            Observation updated = observationRepository.save(existing);
            updateMeasurement(existing, request.getMeasurement());
            updateImages(existing, request.getImages());
        updateTags(existing, request.getTags());
            return reloadObservation(updated.getId());
        });
    }

    /**
     * Removes an observation if it exists.
     *
     * @param id observation identifier
     * @return {@code true} when deletion succeeded
     */
    public boolean deleteObservation(UUID id) {
        return observationRepository.findById(id).map(observation -> {
            observationRepository.delete(observation);
            return true;
        }).orElse(false);
    }

    private Observation buildObservationFromRequest(Crowdsourced request) {
        Observation observation = new Observation();
        observation.setPostcode(request.getPostcode());

        if (request.getValidated() != null) {
            observation.setValidated(request.getValidated());
        }
        observation.setNotes(request.getNotes());
        if (request.getSubmittedAt() != null) {
            observation.setSubmittedAt(request.getSubmittedAt());
        }
        observation.setCitizenUniqueId(request.getCitizenUniqueId());

        return observation;
    }

    private void persistMeasurementIfPresent(MeasurementPayload payload, Observation observation) {
        if (payload == null) {
            return;
        }

        Measurement measurement = new Measurement();
        measurement.setObservation(observation);
        measurement.setTemperatureC(payload.getTemperatureC());
        measurement.setpH(payload.getpH());
        measurement.setAlkalinityMgPerL(payload.getAlkalinityMgPerL());
        measurement.setTurbidityNtu(payload.getTurbidityNtu());

        measurementRepository.save(measurement);
        observation.setMeasurement(measurement);
    }

    private void persistImagesIfPresent(List<ImagePayload> imagePayloads, Observation observation) {
        if (imagePayloads == null || imagePayloads.isEmpty()) {
            return;
        }

        List<Image> images = imagePayloads.stream()
                .map(payload -> buildImage(payload, observation))
                .collect(Collectors.toList());

        imageRepository.saveAll(images);
        observation.setImages(images);
    }

    private Image buildImage(ImagePayload payload, Observation observation) {
        Image image = new Image();
        image.setObservation(observation);
        image.setFilename(payload.getFilename());
        image.setFilePath(payload.getFilePath());
        return image;
    }

    private void persistTags(List<String> tagNames, Observation observation) {
        List<ObservationObservation> links = createTagLinks(tagNames, observation);

        if (links.isEmpty()) {
            return;
        }

        tagLinkRepository.saveAll(links);
    }

    private void applyObservationUpdates(Observation observation, Crowdsourced request) {
        if (request.getPostcode() != null) {
            observation.setPostcode(request.getPostcode());
        }

        if (request.getValidated() != null) {
            observation.setValidated(request.getValidated());
        }
        observation.setNotes(request.getNotes());
        if (request.getSubmittedAt() != null) {
            observation.setSubmittedAt(request.getSubmittedAt());
        }
        if (request.getCitizenUniqueId() != null) {
            observation.setCitizenUniqueId(request.getCitizenUniqueId());
        }
    }

    private void updateMeasurement(Observation observation, MeasurementPayload payload) {
        Measurement existingMeasurement = observation.getMeasurement();

        if (payload == null || (payload.getTemperatureC() == null
                && payload.getpH() == null
                && payload.getAlkalinityMgPerL() == null
                && payload.getTurbidityNtu() == null)) {
            if (existingMeasurement != null) {
                measurementRepository.delete(existingMeasurement);
                observation.setMeasurement(null);
            }
            return;
        }

        if (existingMeasurement == null) {
            existingMeasurement = new Measurement();
            existingMeasurement.setObservation(observation);
        }

        existingMeasurement.setTemperatureC(payload.getTemperatureC());
        existingMeasurement.setpH(payload.getpH());
        existingMeasurement.setAlkalinityMgPerL(payload.getAlkalinityMgPerL());
        existingMeasurement.setTurbidityNtu(payload.getTurbidityNtu());

        measurementRepository.save(existingMeasurement);
        observation.setMeasurement(existingMeasurement);
    }

    private void updateImages(Observation observation, List<ImagePayload> payloads) {
        List<Image> currentImages = new ArrayList<>(observation.getImages());

        if (payloads == null) {
            imageRepository.deleteAll(currentImages);
            observation.getImages().clear();
            return;
        }

        imageRepository.deleteAll(currentImages);
        observation.getImages().clear();

        List<Image> newImages = payloads.stream()
                .map(payload -> buildImage(payload, observation))
                .collect(Collectors.toList());

        if (!newImages.isEmpty()) {
            imageRepository.saveAll(newImages);
        }

        observation.setImages(newImages);
    }

    private void updateTags(Observation observation, List<String> tagNames) {
        List<ObservationObservation> currentLinks = new ArrayList<>(observation.getTagLinks());
        tagLinkRepository.deleteAll(currentLinks);
        observation.getTagLinks().clear();

        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        List<ObservationObservation> newLinks = createTagLinks(tagNames, observation);

        if (!newLinks.isEmpty()) {
            tagLinkRepository.saveAll(newLinks);
        }
    }

    private List<ObservationObservation> createTagLinks(List<String> tagNames, Observation observation) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Collections.emptyList();
        }
        return tagNames.stream()
                .map(this::canonicaliseTagName)
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .map(canonicalName -> buildTagLinkForCanonicalName(canonicalName, observation))
                .collect(Collectors.toList());
    }

    private String canonicaliseTagName(String tagName) {
        if (tagName == null) {
            return null;
        }
        return ObservationTagType.fromValue(tagName)
                .map(ObservationTagType::label)
                .orElseGet(() -> tagName.trim().replaceAll("\\s+", " ").toLowerCase(Locale.UK));
    }

    private ObservationObservation buildTagLinkForCanonicalName(String canonicalTagName, Observation observation) {
        if (canonicalTagName == null || canonicalTagName.isEmpty()) {
            throw new IllegalArgumentException("Observation tags cannot be blank.");
        }

        ObservationTag tag = observationTagRepository.findByNameIgnoreCase(canonicalTagName)
                .orElseThrow(() -> new EntityNotFoundException("Observation tag not found with name: " + canonicalTagName));
        ObservationObservation link = new ObservationObservation();
        link.setObservation(observation);
        link.setObservationTag(tag);
        return link;
    }

    private Observation reloadObservation(UUID id) {
        return observationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Observation not found with id: " + id));
    }
}

