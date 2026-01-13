package com.waterQualityMonitoring.crowdsourced.dto;

/**
 * Generic wrapper returned by the API to include contextual messages alongside
 * payload data.
 *
 * @param <T> type of the response body embedded in the API response
 */
public class ApiResponse<T> {

    private final String message;
    private final T data;

    /**
     * Constructs a response wrapper with a message and data.
     *
     * @param message human readable description of the operation result
     * @param data    payload produced by the API operation
     */
    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
