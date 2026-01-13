package com.waterQualityMonitoring.crowdsourced.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;       
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA aggregate representing a validated or pending citizen observation.
 * <p>
 * Maintains relationships to measurements, images and tag link entities. Helper
 * methods ensure bidirectional associations remain in sync.
 * </p>
 */
@Entity
@Table(name = "observations")
public class Observation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "postcode", nullable = false)
    @NotBlank(message = "Postcode is required")
    private String postcode;

    @Column(name = "citizen_unique_id", nullable = false)
    @NotBlank(message = "Citizen unique id is required")
    private String citizenUniqueId;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt = Instant.now();

    private Boolean validated;

    private String notes;

    @OneToOne(mappedBy = "observation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Measurement measurement;

    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObservationObservation> tagLinks = new ArrayList<>();

    // helpers to set bidirectional relationships between observation and citizen and measurement and images and tags + getters and setters
    public void setCitizenUniqueId(String citizenUniqueId) {
        this.citizenUniqueId = citizenUniqueId;
    }
    public String getCitizenUniqueId() {
        return citizenUniqueId;
    }
    /**
     * Associates a measurement entity with this observation, updating both sides
     * of the relationship.
     *
     * @param measurement measurement entity to attach
     */
    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
        measurement.setObservation(this);
    }
    public void setImages(List<Image> images) {
        this.images.clear();
        if (images != null) {
            images.forEach(this::addImage);
        }
    }

    public void replaceTags(List<ObservationTag> tags) {
        this.tagLinks.clear();
        if (tags != null) {
            tags.forEach(this::addTag);
        }
    }

    /**
     * Adds an image to the observation while updating the owning side.
     *
     * @param image image entity to relate
     */
    public void addImage(Image image) {
        if (image == null) {
            return;
        }
        image.setObservation(this);
        images.add(image);
    }

    /**
     * Adds a tag link based on an {@link ObservationTag} entity.
     *
     * @param tag the tag to add
     */
    public void addTag(ObservationTag tag) {
        if (tag == null) {
            return;
        }
        ObservationObservation link = new ObservationObservation();
        link.setObservation(this);
        link.setObservationTag(tag);
    }

    /**
     * Adds an already constructed {@link ObservationObservation} link.
     *
     * @param link the link to the tag
     */
    public void addTag(ObservationObservation link) {
        if (link == null) {
            return;
        }
        link.setObservation(this);
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getPostcode() {
        return postcode;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    public Instant getSubmittedAt() {
        return submittedAt;
    }
    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
    public Boolean getValidated() {
        return validated;
    }
    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Measurement getMeasurement() {
        return measurement;
    }
    public List<Image> getImages() {
        return images;
    }
    public List<ObservationObservation> getTagLinks() {
        return tagLinks;
    }

}
