package com.waterQualityMonitoring.crowdsourced.dto;

import java.util.List;

/**
 * Generic page wrapper mirroring Spring Data pagination metadata while
 * remaining serializable for API output.
 *
 * @param <T> element type contained in the response content
 */
public class PagedResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    /**
     * Creates a new page response.
     *
     * @param content       list of elements present in the current page
     * @param page          zero-based page index
     * @param size          page size used for retrieval
     * @param totalElements total elements across all pages
     * @param totalPages    number of pages available
     */
    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}

