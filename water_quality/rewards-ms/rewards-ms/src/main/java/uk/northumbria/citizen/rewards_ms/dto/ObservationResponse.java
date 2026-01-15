package uk.northumbria.citizen.rewards_ms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing an observation response from the crowdsourced-data-ms.
 * This class maps the JSON response structure from the observations service.
 * Matches the structure defined in crowdsourced-data-ms ObservationResponse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservationResponse {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("citizenId")
    private String citizenId;
    
    @JsonProperty("postcode")
    private String postcode;
    
    @JsonProperty("temperature")
    private Double temperature;
    
    @JsonProperty("ph")
    private Double ph;
    
    @JsonProperty("alkalinity")
    private Double alkalinity;
    
    @JsonProperty("turbidity")
    private Double turbidity;
    
    @JsonProperty("observations")
    private List<String> observations;
    
    @JsonProperty("imagePaths")
    private List<String> imagePaths;
    
    @JsonProperty("submittedAt")
    private LocalDateTime submittedAt;
    
    @JsonProperty("valid")
    private Boolean valid;
}

