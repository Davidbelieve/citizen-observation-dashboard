package com.waterQualityMonitoring.crowdsourced.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.waterQualityMonitoring.crowdsourced.model.ObservationTag;
import com.waterQualityMonitoring.crowdsourced.model.ObservationTagType;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationTagRepository;

/**
 * Ensures the observation tag catalogue is populated with the supported values at startup.
 */
@Component
public class ObservationTagDataInitializer implements ApplicationRunner {

    private final ObservationTagRepository observationTagRepository;

    public ObservationTagDataInitializer(ObservationTagRepository observationTagRepository) {
        this.observationTagRepository = observationTagRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (ObservationTagType type : ObservationTagType.values()) {
            observationTagRepository.findByNameIgnoreCase(type.label())
                    .orElseGet(() -> observationTagRepository.save(createTag(type.label())));
        }
    }

    private ObservationTag createTag(String name) {
        ObservationTag tag = new ObservationTag();
        tag.setName(name);
        return tag;
    }
}

