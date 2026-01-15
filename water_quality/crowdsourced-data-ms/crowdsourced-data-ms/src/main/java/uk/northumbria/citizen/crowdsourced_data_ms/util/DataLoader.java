package uk.northumbria.citizen.crowdsourced_data_ms.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.northumbria.citizen.crowdsourced_data_ms.model.Observation;
import uk.northumbria.citizen.crowdsourced_data_ms.repo.ObservationRepository;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final ObservationRepository observationRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (observationRepository.count() == 0) {
            log.info("Loading default observation data...");
            
            Observation defaultObservation = Observation.builder()
                    .citizenId("C200")
                    .postcode("NE1 7ST")
                    .temperature(26.3)
                    .ph(7.2)
                    .observations(Arrays.asList("Clear water", "Good quality"))
                    .imagePaths(Arrays.asList("image1.jpg"))
                    .valid(true)
                    .build();
            
            observationRepository.save(defaultObservation);
            log.info("Default observation data loaded successfully!");
        } else {
            log.info("Observation data already exists. Skipping data load.");
        }
    }
}

