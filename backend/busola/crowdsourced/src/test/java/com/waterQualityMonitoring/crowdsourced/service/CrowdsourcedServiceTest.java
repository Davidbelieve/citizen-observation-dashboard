package com.waterQualityMonitoring.crowdsourced.service;

import static com.waterQualityMonitoring.crowdsourced.testsupport.TestDataFactory.newCrowdsourcedRequest;
import static com.waterQualityMonitoring.crowdsourced.testsupport.TestDataFactory.newCrowdsourcedRequestWithMeasurement;
import static com.waterQualityMonitoring.crowdsourced.testsupport.TestDataFactory.newImage;
import static com.waterQualityMonitoring.crowdsourced.testsupport.TestDataFactory.newMeasurement;
import static com.waterQualityMonitoring.crowdsourced.testsupport.TestDataFactory.newObservation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.waterQualityMonitoring.crowdsourced.model.Crowdsourced;
import com.waterQualityMonitoring.crowdsourced.model.Measurement;
import com.waterQualityMonitoring.crowdsourced.model.Observation;
import com.waterQualityMonitoring.crowdsourced.model.ObservationObservation;
import com.waterQualityMonitoring.crowdsourced.model.ObservationTag;
import com.waterQualityMonitoring.crowdsourced.repository.ImageRepository;
import com.waterQualityMonitoring.crowdsourced.repository.MeasurementRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationObservationRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationRepository;
import com.waterQualityMonitoring.crowdsourced.repository.ObservationTagRepository;

@ExtendWith(MockitoExtension.class)
class CrowdsourcedServiceTest {

    @Mock
    private ObservationRepository observationRepository;
    @Mock
    private ObservationTagRepository observationTagRepository;
    @Mock
    private ObservationObservationRepository tagLinkRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private MeasurementRepository measurementRepository;

    @InjectMocks
    private CrowdsourcedService service;

    private String citizenUniqueId;

    @BeforeEach
    void setUp() {
        citizenUniqueId = "EXT-" + java.util.UUID.randomUUID();
    }

    private Crowdsourced minimalRequest() {
        Crowdsourced request = newCrowdsourcedRequest(citizenUniqueId);
        request.setNotes("Notes");
        request.setSubmittedAt(Instant.parse("2025-01-01T10:15:30Z"));
        request.setTags(List.of("Clear"));
        return request;
    }

    private Observation savedObservation() {
        Observation observation = newObservation(citizenUniqueId);
        observation.setId(java.util.UUID.randomUUID());
        return observation;
    }

    @Nested
    class CreateObservation {

        @Test
        void shouldCreateObservationWithEntities() {
            Crowdsourced request = newCrowdsourcedRequestWithMeasurement(citizenUniqueId);
            request.setNotes("Notes");
            request.setSubmittedAt(Instant.parse("2025-01-01T10:15:30Z"));
            request.setTags(List.of("Cloudy"));

            Observation persisted = savedObservation();

            given(observationRepository.save(any(Observation.class))).willReturn(persisted);
            given(observationRepository.findById(persisted.getId())).willReturn(Optional.of(persisted));
            given(observationTagRepository.findByNameIgnoreCase("Cloudy")).willReturn(Optional.of(new ObservationTag() {{
                setId(42);
                setName("Cloudy");
            }}));

            Observation result = service.createObservation(request);

            assertThat(result).isSameAs(persisted);

            ArgumentCaptor<Observation> observationCaptor = ArgumentCaptor.forClass(Observation.class);
            verify(observationRepository).save(observationCaptor.capture());

            Observation saved = observationCaptor.getValue();
            assertThat(saved.getCitizenUniqueId()).isEqualTo(citizenUniqueId);
            assertThat(saved.getPostcode()).isEqualTo("NE1 4ST");
            assertThat(saved.getValidated()).isTrue();
            assertThat(saved.getNotes()).isEqualTo("Notes");
            assertThat(saved.getSubmittedAt()).isEqualTo(Instant.parse("2025-01-01T10:15:30Z"));

            verify(measurementRepository).save(any(Measurement.class));
            verify(imageRepository).saveAll(any());
            verify(tagLinkRepository).saveAll(any());
        }

        @Test
        void shouldDeduplicateTagsBeforePersistence() {
            Crowdsourced request = minimalRequest();
            request.setTags(List.of("Clear", "clear", "  Clear  "));

            Observation persisted = savedObservation();
            given(observationRepository.save(any(Observation.class))).willReturn(persisted);
            given(observationRepository.findById(persisted.getId())).willReturn(Optional.of(persisted));
            given(observationTagRepository.findByNameIgnoreCase("Clear")).willReturn(Optional.of(new ObservationTag() {{
                setId(1);
                setName("Clear");
            }}));

            service.createObservation(request);

            verify(observationTagRepository, times(1)).findByNameIgnoreCase("Clear");
            verify(tagLinkRepository).saveAll(any());
        }

    }

    @Nested
    class UpdateObservation {

        @Test
        void shouldUpdateObservationAndRelatedEntities() {
            Crowdsourced request = newCrowdsourcedRequestWithMeasurement(citizenUniqueId);
            request.getMeasurement().setTemperatureC(14.2);
            request.setTags(List.of("Foamy"));

            Observation existing = savedObservation();
            existing.setMeasurement(newMeasurement(existing));
            existing.getImages().add(newImage(existing));
            ObservationObservation existingLink = new ObservationObservation();
            existingLink.setObservation(existing);
            ObservationTag existingTag = new ObservationTag();
            existingTag.setId(10);
            existingTag.setName("Existing");
            existingLink.setObservationTag(existingTag);

            UUID observationId = existing.getId();

            given(observationRepository.findById(observationId))
                    .willReturn(Optional.of(existing));
            given(observationRepository.save(existing))
                    .willReturn(existing);
            given(observationRepository.findById(observationId))
                    .willReturn(Optional.of(existing));
            given(observationTagRepository.findByNameIgnoreCase("Foamy"))
                    .willReturn(Optional.of(new ObservationTag() {{
                        setId(34);
                        setName("Foamy");
                    }}));

            Optional<Observation> result = service.updateObservation(observationId, request);

            assertThat(result).isPresent().contains(existing);

            verify(measurementRepository).save(any(Measurement.class));
            verify(imageRepository).deleteAll(any());
            verify(imageRepository).saveAll(any());
            verify(tagLinkRepository).deleteAll(any());
            verify(tagLinkRepository).saveAll(any());
        }

        @Test
        void shouldUpdateObservationWhenTagsRemoved() {
            Crowdsourced request = newCrowdsourcedRequest(citizenUniqueId);
            request.setTags(null);

            Observation existing = savedObservation();
            existing.getTagLinks().add(new ObservationObservation());

            given(observationRepository.findById(existing.getId())).willReturn(Optional.of(existing));
            given(observationRepository.save(existing)).willReturn(existing);
            given(observationRepository.findById(existing.getId())).willReturn(Optional.of(existing));

            Optional<Observation> result = service.updateObservation(existing.getId(), request);

            assertThat(result).isPresent().contains(existing);
            verify(tagLinkRepository).deleteAll(any());
        }

        @Test
        void shouldReturnEmptyWhenObservationMissing() {
            UUID observationId = UUID.randomUUID();
            Crowdsourced request = minimalRequest();

            given(observationRepository.findById(observationId)).willReturn(Optional.empty());

            Optional<Observation> result = service.updateObservation(observationId, request);

            assertThat(result).isEmpty();
            verifyNoInteractions(measurementRepository, imageRepository, tagLinkRepository);
        }
    }

    @Test
    void shouldDeleteObservationWhenPresent() {
        Observation existing = savedObservation();
        UUID observationId = existing.getId();
        given(observationRepository.findById(observationId)).willReturn(Optional.of(existing));

        boolean deleted = service.deleteObservation(observationId);

        assertThat(deleted).isTrue();
        verify(observationRepository).delete(existing);
    }

    @Test
    void shouldReturnFalseWhenObservationToDeleteMissing() {
        UUID observationId = UUID.randomUUID();
        given(observationRepository.findById(observationId)).willReturn(Optional.empty());

        boolean deleted = service.deleteObservation(observationId);

        assertThat(deleted).isFalse();
        verify(observationRepository).findById(observationId);
        verifyNoMoreInteractions(observationRepository);
    }

    @Test
    void shouldGetObservationPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Observation observation = savedObservation();
        given(observationRepository.findAll(pageable)).willReturn(new PageImpl<>(List.of(observation)));

        Page<Observation> page = service.getObservations(pageable);

        assertThat(page.getContent()).containsExactly(observation);
    }

    @Test
    void shouldGetObservationOptional() {
        Observation observation = savedObservation();
        given(observationRepository.findById(observation.getId())).willReturn(Optional.of(observation));

        Optional<Observation> result = service.getObservation(observation.getId());

        assertThat(result).contains(observation);
    }
}

