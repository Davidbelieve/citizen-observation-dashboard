package uk.northumbria.citizen.crowdsourced_data_ms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "observations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Observation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String citizenId;
    
    @Column(nullable = false)
    private String postcode;
    
    private Double temperature;
    
    private Double ph;
    
    private Double alkalinity;
    
    private Double turbidity;
    
    @ElementCollection
    @CollectionTable(name = "observation_notes", joinColumns = @JoinColumn(name = "observation_id"))
    @Column(name = "note")
    private List<String> observations = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "observation_images", joinColumns = @JoinColumn(name = "observation_id"))
    @Column(name = "image_path")
    private List<String> imagePaths = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    @Column(nullable = false)
    private Boolean valid;
    
    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }
}

