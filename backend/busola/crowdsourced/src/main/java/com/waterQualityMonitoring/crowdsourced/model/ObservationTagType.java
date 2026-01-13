package com.waterQualityMonitoring.crowdsourced.model;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Enumeration of supported observation tags supplied by citizens.
 */
public enum ObservationTagType {
    CLEAR("Clear"),
    CLOUDY("Cloudy"),
    MURKY("Murky"),
    FOAMY("Foamy"),
    OILY("Oily"),
    DISCOLOURED("Discoloured"),
    PRESENCE_OF_ODOUR("Presence of Odour");

    private final String label;

    ObservationTagType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static Optional<ObservationTagType> fromValue(String value) {
        if (value == null) {
            return Optional.empty();
        }
        String normalised = value.trim().replaceAll("\\s+", " ").toUpperCase(Locale.UK);
        return Arrays.stream(values())
                .filter(type -> type.label.toUpperCase(Locale.UK).equals(normalised))
                .findFirst();
    }

    public static List<String> labels() {
        return Arrays.stream(values())
                .map(ObservationTagType::label)
                .collect(Collectors.toList());
    }
}

