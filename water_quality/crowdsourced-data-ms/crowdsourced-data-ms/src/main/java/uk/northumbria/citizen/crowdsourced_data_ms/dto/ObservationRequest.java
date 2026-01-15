package uk.northumbria.citizen.crowdsourced_data_ms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservationRequest {
    
    @NotBlank(message = "Citizen ID is required")
    private String citizenId;
    
    @NotBlank(message = "Postcode is required")
    private String postcode;
    
    private Double temperature;
    
    private Double ph;
    
    private Double alkalinity;
    
    private Double turbidity;
    
    private List<String> observations = new ArrayList<>();
    
    private List<String> imagePaths = new ArrayList<>();
}

