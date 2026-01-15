package uk.northumbria.citizen.crowdsourced_data_ms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.northumbria.citizen.crowdsourced_data_ms.dto.ObservationRequest;
import uk.northumbria.citizen.crowdsourced_data_ms.dto.ObservationResponse;
import uk.northumbria.citizen.crowdsourced_data_ms.service.ObservationService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/observations")
@RequiredArgsConstructor
public class ObservationController {
    
    private final ObservationService observationService;
    
    @PostMapping
    public ResponseEntity<ObservationResponse> createObservation(@Valid @RequestBody ObservationRequest request) {
        ObservationResponse response = observationService.createObservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ObservationResponse>> getAllObservations() {
        List<ObservationResponse> responses = observationService.getAll();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ObservationResponse> getObservationById(@PathVariable Long id) {
        ObservationResponse response = observationService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<List<ObservationResponse>> getObservationsByCitizenId(@PathVariable String citizenId) {
        List<ObservationResponse> responses = observationService.getByCitizenId(citizenId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/postcode/{postcode}")
    public ResponseEntity<List<ObservationResponse>> getObservationsByPostcode(@PathVariable String postcode) {
        List<ObservationResponse> responses = observationService.getByPostcode(postcode);
        return ResponseEntity.ok(responses);
    }
}

