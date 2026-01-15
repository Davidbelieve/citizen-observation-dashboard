package uk.northumbria.citizen.crowdsourced_data_ms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObservationResponse {
    
    private Long id;
    private String citizenId;
    private String postcode;
    private Double temperature;
    private Double ph;
    private Double alkalinity;
    private Double turbidity;
    private List<String> observations = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();
    private LocalDateTime submittedAt;
    private Boolean valid;
}

