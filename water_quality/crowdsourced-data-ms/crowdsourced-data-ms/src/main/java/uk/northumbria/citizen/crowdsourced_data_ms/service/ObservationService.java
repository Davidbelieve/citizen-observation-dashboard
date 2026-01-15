package uk.northumbria.citizen.crowdsourced_data_ms.service;

import uk.northumbria.citizen.crowdsourced_data_ms.dto.ObservationRequest;
import uk.northumbria.citizen.crowdsourced_data_ms.dto.ObservationResponse;

import java.util.List;

public interface ObservationService {
    ObservationResponse createObservation(ObservationRequest request);
    ObservationResponse getById(Long id);
    List<ObservationResponse> getAll();
    List<ObservationResponse> getByCitizenId(String citizenId);
    List<ObservationResponse> getByPostcode(String postcode);
}

