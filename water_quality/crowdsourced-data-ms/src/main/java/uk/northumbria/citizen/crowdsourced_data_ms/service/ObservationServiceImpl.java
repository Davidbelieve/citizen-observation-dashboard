package uk.northumbria.citizen.crowdsourced_data_ms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.northumbria.citizen.crowdsourced_data_ms.dto.ObservationRequest;
import uk.northumbria.citizen.crowdsourced_data_ms.dto.ObservationResponse;
import uk.northumbria.citizen.crowdsourced_data_ms.model.Observation;
import uk.northumbria.citizen.crowdsourced_data_ms.repo.ObservationRepository;
import uk.northumbria.citizen.crowdsourced_data_ms.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ObservationServiceImpl implements ObservationService {
    
    private final ObservationRepository observationRepository;
    
    @Override
    public ObservationResponse createObservation(ObservationRequest request, org.springframework.web.multipart.MultipartFile imageFile) {
        List<String> imagePaths = request.getImagePaths() != null ? request.getImagePaths() : new ArrayList<>();
        
        // Handle file upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads");
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                java.nio.file.Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                imagePaths.add("uploads/" + fileName);
            } catch (java.io.IOException e) {
                e.printStackTrace(); // Log error but continue
            }
        }

        // Build Observation entity from request
        Observation observation = Observation.builder()
                .citizenId(request.getCitizenId() != null ? request.getCitizenId().trim() : null)
                .postcode(request.getPostcode() != null ? request.getPostcode().trim() : null)
                .temperature(request.getTemperature())
                .ph(request.getPh())
                .alkalinity(request.getAlkalinity())
                .turbidity(request.getTurbidity())
                .observations(request.getObservations() != null ? request.getObservations() : new ArrayList<>())
                .imagePaths(imagePaths)
                .build();
        
        // Apply validation
        boolean isValid = ValidationUtil.isValid(observation);
        observation.setValid(isValid);
        
        // Save record via Repository
        Observation savedObservation = observationRepository.save(observation);
        
        // Convert to ObservationResponse
        return convertToResponse(savedObservation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ObservationResponse getById(Long id) {
        Observation observation = observationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Observation not found with id: " + id));
        return convertToResponse(observation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ObservationResponse> getAll() {
        return observationRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ObservationResponse> getByCitizenId(String citizenId) {
        String trimmedId = citizenId != null ? citizenId.trim() : "";
        return observationRepository.findByCitizenIdIgnoreCase(trimmedId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ObservationResponse> getByPostcode(String postcode) {
        return observationRepository.findByPostcode(postcode).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private ObservationResponse convertToResponse(Observation observation) {
        return ObservationResponse.builder()
                .id(observation.getId())
                .citizenId(observation.getCitizenId())
                .postcode(observation.getPostcode())
                .temperature(observation.getTemperature())
                .ph(observation.getPh())
                .alkalinity(observation.getAlkalinity())
                .turbidity(observation.getTurbidity())
                .observations(observation.getObservations() != null ? observation.getObservations() : new ArrayList<>())
                .imagePaths(observation.getImagePaths() != null ? observation.getImagePaths() : new ArrayList<>())
                .submittedAt(observation.getSubmittedAt())
                .valid(observation.getValid())
                .build();
    }
}

