package com.waterQualityMonitoring.crowdsourced.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

/**
 * Catalog entity describing classification tags that can be assigned to
 * observations.
 */
@Entity
@Table(name = "observation_tags")
public class ObservationTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    // getters and setters
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    // join table for observation and observation tag
    @OneToMany(mappedBy = "observationTag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObservationObservation> observationLinks = new ArrayList<>();

    public List<ObservationObservation> getObservationLinks() {
        return observationLinks;
    }

    public void setObservationLinks(List<ObservationObservation> links) {
        this.observationLinks.clear();
        if (links != null) {
            links.forEach(this::addObservationLink);
        }
    }

    public void addObservationLink(ObservationObservation link) {
        if (link == null) {
            return;
        }
        link.setObservationTag(this);
        observationLinks.add(link);
    }
}
