package com.waterQualityMonitoring.reward.service;

import java.util.List;

import com.waterQualityMonitoring.reward.dto.CrowdsourcedObservation;

/**
 * Abstraction for retrieving validated observations from the crowdsourced data
 * service.
 */
public interface CrowdsourcedObservationClient {

    /**
     * Fetches validated observations ready for reward processing.
     *
     * @return list of validated observations
     */
    List<CrowdsourcedObservation> fetchValidatedObservations();
}

