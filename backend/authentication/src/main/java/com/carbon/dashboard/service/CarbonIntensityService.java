package com.carbon.dashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.util.Map;
import java.util.List;
import java.util.Date;

/**
 * Service class for interacting with the UK Carbon Intensity API.
 * Fetches real-time carbon intensity data and calculates averages.
 * 
 * API Documentation: https://api.carbonintensity.org.uk/
 * 
 * Key Features:
 * - Fetches current carbon intensity forecast and actual data
 * - Retrieves generation mix factors (coal, gas, wind, solar, etc.)
 * - Calculates average intensity on-the-fly (NOT stored in database)
 * - Provides intensity data by specific dates
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@Service
public class CarbonIntensityService {
    
    /**
     * Base URL for the Carbon Intensity API.
     */
    private static final String API_BASE_URL = "https://api.carbonintensity.org.uk";
    
    /**
     * RestTemplate for making HTTP requests to external API.
     */
    private final RestTemplate restTemplate;
    
    /**
     * Constructor initializes RestTemplate.
     */
    public CarbonIntensityService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Fetches current carbon intensity data from the API.
     * Returns both forecast and actual intensity values.
     * 
     * Endpoint: GET /intensity
     * 
     * @return Map containing API response with intensity data
     * @throws RuntimeException if API request fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCurrentIntensity() {
        try {
            String url = API_BASE_URL + "/intensity";
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch carbon intensity data: " + e.getMessage());
        }
    }
    
    /**
     * Fetches generation mix factors showing percentage breakdown.
     * Shows how much electricity comes from each source (coal, gas, wind, etc.).
     * 
     * Endpoint: GET /intensity/factors
     * 
     * @return Map containing generation mix percentages
     * @throws RuntimeException if API request fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getIntensityFactors() {
        try {
            String url = API_BASE_URL + "/intensity/factors";
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch intensity factors: " + e.getMessage());
        }
    }
    
    /**
     * Fetches intensity data for a specific date.
     * 
     * Endpoint: GET /intensity/date/{date}
     * 
     * @param date the date in format YYYY-MM-DD (e.g., "2025-10-26")
     * @return Map containing intensity data for the specified date
     * @throws RuntimeException if API request fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getIntensityByDate(String date) {
        try {
            String url = API_BASE_URL + "/intensity/date/" + date;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch data for date: " + e.getMessage());
        }
    }
    
    /**
     * Calculates average carbon intensity from current API data.
     * 
     * IMPORTANT: Average is calculated on-the-fly and NOT stored in database.
     * This fulfills the requirement: "Average data should be calculated and not stored".
     * 
     * Process:
     * 1. Fetch current intensity data from API
     * 2. Extract actual intensity values from data array
     * 3. Calculate mean average
     * 4. Return result with metadata
     * 
     * @return Map containing average intensity, data point count, and timestamp
     * @throws RuntimeException if calculation fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAverageIntensityData() {
        try {
            // Fetch current data from API
            Map<String, Object> response = getCurrentIntensity();
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
            
            if (data != null && !data.isEmpty()) {
                double sum = 0;
                int count = 0;
                
                // Iterate through each data point
                for (Map<String, Object> item : data) {
                    Map<String, Object> intensity = (Map<String, Object>) item.get("intensity");
                    
                    if (intensity != null && intensity.get("actual") != null) {
                        // Add actual intensity value to sum
                        sum += ((Number) intensity.get("actual")).doubleValue();
                        count++;
                    }
                }
                
                // Calculate average
                double average = count > 0 ? sum / count : 0;
                
                // Return calculated data (NOT stored in database)
                return Map.of(
                    "averageIntensity", average,
                    "dataPoints", count,
                    "unit", "gCO2/kWh",
                    "timestamp", new Date(),
                    "calculated", true,  // Flag showing this is calculated, not stored
                    "message", "Average calculated from " + count + " data points"
                );
            }
            
            // No data available
            return Map.of(
                "error", "No data available",
                "averageIntensity", 0,
                "dataPoints", 0
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate average: " + e.getMessage());
        }
    }
    
    /**
     * Fetches regional carbon intensity data from the API.
     * Returns intensity data for all regions in the UK.
     * 
     * Endpoint: GET /regional
     * 
     * @return Map containing API response with regional intensity data
     * @throws RuntimeException if API request fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRegionalIntensity() {
        try {
            String url = API_BASE_URL + "/regional";
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch regional carbon intensity data: " + e.getMessage());
        }
    }
    
    /**
     * Fetches regional carbon intensity data for a specific region.
     * 
     * Endpoint: GET /regional/{region}
     * 
     * @param region the region name (e.g., "england", "scotland", "wales")
     * @return Map containing API response with regional intensity data
     * @throws RuntimeException if API request fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRegionalIntensityByRegion(String region) {
        try {
            String url = API_BASE_URL + "/regional/" + region;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch regional data for " + region + ": " + e.getMessage());
        }
    }
    
    /**
     * Fetches regional carbon intensity data by postcode.
     * 
     * Endpoint: GET /regional/postcode/{postcode}
     * 
     * @param postcode the UK postcode
     * @return Map containing API response with regional intensity data
     * @throws RuntimeException if API request fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRegionalIntensityByPostcode(String postcode) {
        try {
            String url = API_BASE_URL + "/regional/postcode/" + postcode;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch regional data for postcode " + postcode + ": " + e.getMessage());
        }
    }
    
    /**
     * Fetches regional carbon intensity data by region ID.
     * 
     * Endpoint: GET /regional/regionid/{regionId}
     * 
     * @param regionId the region ID
     * @return Map containing API response with regional intensity data
     * @throws RuntimeException if API request fails
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRegionalIntensityByRegionId(String regionId) {
        try {
            String url = API_BASE_URL + "/regional/regionid/" + regionId;
            return restTemplate.getForObject(url, Map.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch regional data for region ID " + regionId + ": " + e.getMessage());
        }
    }
}