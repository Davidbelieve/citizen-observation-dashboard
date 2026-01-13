package com.waterQualityMonitoring.crowdsourced.dto;

import java.time.Instant;

/**
 * Lightweight DTO used to expose error information to API consumers.
 * <p>
 * Encapsulates a timestamp and a human-readable message, keeping the public
 * contract stable regardless of the internal exception type.
 * </p>
 */
public class ApiError {

    private final Instant timestamp = Instant.now();
    private final String message;

    /**
     * Creates an error payload with the supplied message.
     *
     * @param message user friendly error description
     */
    public ApiError(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}

