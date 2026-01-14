package com.waterQualityMonitoring.crowdsourced.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waterQualityMonitoring.crowdsourced.dto.ApiResponse;
import com.waterQualityMonitoring.crowdsourced.dto.ObservationResponse;
import com.waterQualityMonitoring.crowdsourced.dto.PagedResponse;
import com.waterQualityMonitoring.crowdsourced.model.Crowdsourced;
import com.waterQualityMonitoring.crowdsourced.model.Observation;
import com.waterQualityMonitoring.crowdsourced.service.CrowdsourcedService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * REST controller that exposes CRUD operations for crowdsourced water quality
 * observations.
 * <p>
 * Endpoints in this controller offer paging, validation and mapping behaviour
 * tailored to the API contract consumed by external clients and downstream
 * services.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/observations")
@Validated
public class CrowdsourcedController {

    private final CrowdsourcedService crowdsourcedService;

    /**
     * Creates a controller instance backed by the domain service.
     *
     * @param crowdsourcedService domain component responsible for observation
     *                            persistence
     */
    public CrowdsourcedController(CrowdsourcedService crowdsourcedService) {
        this.crowdsourcedService = crowdsourcedService;
    }

    /**
     * Persists a new observation submitted by a citizen.
     *
     * @param request validated observation payload
     * @return HTTP 201 response containing the persisted observation projection
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ObservationResponse>> createObservation(
            @Valid @RequestBody Crowdsourced request) {
        Observation observation = crowdsourcedService.createObservation(request);
        ObservationResponse responseBody = ObservationResponse.fromEntity(observation);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Observation created successfully", responseBody));
    }

    /**
     * Retrieves a paginated collection of observations.
     *
     * @param page zero-based page index
     * @param size page size (between 1 and 100)
     * @return a page wrapper containing mapped observation DTOs
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PagedResponse<ObservationResponse>> getAllObservations(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Observation> observationPage = crowdsourcedService.getObservations(pageable);

        List<ObservationResponse> content = observationPage.getContent().stream()
                .map(ObservationResponse::fromEntity)
                .collect(Collectors.toList());

        PagedResponse<ObservationResponse> responseBody = new PagedResponse<>(
                content,
                observationPage.getNumber(),
                observationPage.getSize(),
                observationPage.getTotalElements(),
                observationPage.getTotalPages());

        return ResponseEntity.ok(responseBody);
    }

    /**
     * Retrieves a single observation by identifier.
     *
     * @param id observation UUID
     * @return 200 with the observation body if found, otherwise 404
     */
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObservationResponse> getObservationById(@PathVariable UUID id) {
        return crowdsourcedService.getObservation(id)
                .map(ObservationResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an observation identified by {@code id}.
     *
     * @param id      observation identifier
     * @param request desired state for the observation
     * @return 200 with the updated observation, or 404 if the entity does not exist
     */
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ObservationResponse> updateObservation(@PathVariable UUID id,
            @Valid @RequestBody Crowdsourced request) {
        return crowdsourcedService.updateObservation(id, request)
                .map(ObservationResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Removes an observation from the data store.
     *
     * @param id observation identifier
     * @return 204 when the observation is deleted or 404 when it does not exist
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteObservation(@PathVariable UUID id) {
        boolean deleted = crowdsourcedService.deleteObservation(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
