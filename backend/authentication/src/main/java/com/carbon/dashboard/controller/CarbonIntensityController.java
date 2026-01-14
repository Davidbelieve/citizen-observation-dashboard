package com.carbon.dashboard.controller;

import com.carbon.dashboard.service.CarbonIntensityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST Controller for Carbon Intensity API endpoints.
 * Provides access to UK electricity carbon emissions data.
 * 
 * Base URL: /api/carbon
 * 
 * ALL ENDPOINTS REQUIRE AUTHENTICATION (JWT token in Authorization header)
 * 
 * Endpoints:
 * - GET /api/carbon/intensity - Current carbon intensity data
 * - GET /api/carbon/factors - Generation mix factors
 * - GET /api/carbon/average - Calculated average intensity (NOT stored)
 * - GET /api/carbon/intensity/date/{date} - Historical data by date
 * - GET /api/carbon/intensity/regional - Regional intensity data for all regions
 * - GET /api/carbon/intensity/regional/{region} - Regional data by region name
 * - GET /api/carbon/intensity/regional/postcode/{postcode} - Regional data by postcode
 * - GET /api/carbon/intensity/regional/regionid/{regionId} - Regional data by region ID
 * 
 * Data Source: https://api.carbonintensity.org.uk
 * 
 * @author Carbon Dashboard Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/carbon")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CarbonIntensityController {
    
    @Autowired
    private CarbonIntensityService carbonService;
    
    /**
     * Gets current carbon intensity data from UK API.
     * Returns forecast and actual intensity values with timestamps.
     * 
     * GET /api/carbon/intensity
     * 
     * Headers Required:
     * Authorization: Bearer <jwt-token>
     * 
     * Success Response (200 OK):
     * {
     *   "data": [
     *     {
     *       "from": "2025-10-26T19:00:00Z",
     *       "to": "2025-10-26T19:30:00Z",
     *       "intensity": {
     *         "forecast": 145,
     *         "actual": 142,
     *         "index": "moderate"
     *       }
     *     }
     *   ]
     * }
     * 
     * Error Response (500):
     * {
     *   "error": "Failed to fetch carbon intensity data"
     * }
     * 
     * @return ResponseEntity with carbon intensity data or error
     */
    @GetMapping("/intensity")
    public ResponseEntity<Map<String, Object>> getCurrentIntensity() {
        try {
            Map<String, Object> data = carbonService.getCurrentIntensity();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Gets generation mix factors from UK API.
     * Shows percentage breakdown of electricity sources (coal, gas, wind, solar, etc.).
     * 
     * GET /api/carbon/factors
     * 
     * Headers Required:
     * Authorization: Bearer <jwt-token>
     * 
     * Success Response (200 OK):
     * {
     *   "data": [
     *     {
     *       "generationmix": [
     *         {"fuel": "gas", "perc": 35.2},
     *         {"fuel": "wind", "perc": 28.5},
     *         {"fuel": "nuclear", "perc": 18.3},
     *         {"fuel": "solar", "perc": 12.1}
     *       ]
     *     }
     *   ]
     * }
     * 
     * @return ResponseEntity with generation mix data or error
     */
    @GetMapping("/factors")
    public ResponseEntity<Map<String, Object>> getFactors() {
        try {
            Map<String, Object> data = carbonService.getIntensityFactors();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Gets calculated average carbon intensity.
     * 
     * IMPORTANT: Average is calculated on-the-fly from API data.
     * NOT stored in any database (as per requirement).
     * 
     * Process:
     * 1. Fetch current intensity data from external API
     * 2. Extract all actual intensity values
     * 3. Calculate mean average: sum / count
     * 4. Return result with metadata
     * 
     * GET /api/carbon/average
     * 
     * Headers Required:
     * Authorization: Bearer <jwt-token>
     * 
     * Success Response (200 OK):
     * {
     *   "averageIntensity": 145.7,
     *   "dataPoints": 8,
     *   "unit": "gCO2/kWh",
     *   "timestamp": "2025-10-26T19:30:00",
     *   "calculated": true,
     *   "message": "Average calculated from 8 data points"
     * }
     * 
     * Note: Each request recalculates the average from fresh API data.
     * 
     * @return ResponseEntity with calculated average or error
     */
    @GetMapping("/average")
    public ResponseEntity<Map<String, Object>> getAverageIntensity() {
        try {
            Map<String, Object> data = carbonService.getAverageIntensityData();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Gets regional carbon intensity data for all regions.
     * Returns intensity data for all regions in the UK.
     * 
     * GET /api/carbon/intensity/regional
     * 
     * Headers Required:
     * Authorization: Bearer <jwt-token>
     * 
     * Success Response (200 OK):
     * {
     *   "data": [
     *     {
     *       "regionid": 1,
     *       "dnoregion": "North Scotland",
     *       "shortname": "North Scotland",
     *       "intensity": {
     *         "forecast": 145,
     *         "actual": 142,
     *         "index": "moderate"
     *       }
     *     }
     *   ]
     * }
     * 
     * @return ResponseEntity with regional intensity data or error
     */
    @GetMapping("/intensity/regional")
    public ResponseEntity<Map<String, Object>> getRegionalIntensity() {
        try {
            Map<String, Object> data = carbonService.getRegionalIntensity();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Gets carbon intensity data for a specific date.
     * Retrieves historical intensity values.
     * 
     * GET /api/carbon/intensity/date/{date}
     * 
     * Headers Required:
     * Authorization: Bearer <jwt-token>
     * 
     * URL Parameter:
     * - date: Format YYYY-MM-DD (e.g., "2025-10-26")
     * 
     * Example Request:
     * GET /api/carbon/intensity/date/2025-10-26
     * 
     * Success Response (200 OK):
     * {
     *   "data": [
     *     {
     *       "from": "2025-10-26T00:00:00Z",
     *       "to": "2025-10-26T00:30:00Z",
     *       "intensity": {
     *         "forecast": 150,
     *         "actual": 148,
     *         "index": "moderate"
     *       }
     *     }
     *   ]
     * }
     * 
     * @param date the date in YYYY-MM-DD format
     * @return ResponseEntity with intensity data for the date or error
     */
    @GetMapping("/intensity/date/{date}")
    public ResponseEntity<Map<String, Object>> getIntensityByDate(@PathVariable String date) {
        try {
            Map<String, Object> data = carbonService.getIntensityByDate(date);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Gets regional carbon intensity data for a specific region.
     * 
     * GET /api/carbon/intensity/regional/{region}
     * 
     * Headers Required:
     * Authorization: Bearer <jwt-token>
     * 
     * URL Parameter:
     * - region: Region name (e.g., "england", "scotland", "wales")
     * 
     * @param region the region name
     * @return ResponseEntity with regional intensity data or error
     */
    @GetMapping("/intensity/regional/{region}")
    public ResponseEntity<Map<String, Object>> getRegionalIntensityByRegion(@PathVariable String region) {
        try {
            Map<String, Object> data = carbonService.getRegionalIntensityByRegion(region);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Gets regional carbon intensity data by postcode.
     * 
     * GET /api/carbon/intensity/regional/postcode/{postcode}
     * 
     * Headers Required:
     * Authorization: Bearer <jwt-token>
     * 
     * URL Parameter:
     * - postcode: UK postcode (e.g., "SW1A1AA")
     * 
     * @param postcode the UK postcode
     * @return ResponseEntity with regional intensity data or error
     */
    @GetMapping("/intensity/regional/postcode/{postcode}")
    public ResponseEntity<Map<String, Object>> getRegionalIntensityByPostcode(@PathVariable String postcode) {
        try {
            Map<String, Object> data = carbonService.getRegionalIntensityByPostcode(postcode);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Gets regional carbon intensity data by region ID.
     * 
     * GET /api/carbon/intensity/regional/regionid/{regionId}
     * 
     * Headers Required:
     * Authorization: Bearer <jwt-token>
     * 
     * URL Parameter:
     * - regionId: Region ID (e.g., "1", "2", "3")
     * 
     * @param regionId the region ID
     * @return ResponseEntity with regional intensity data or error
     */
    @GetMapping("/intensity/regional/regionid/{regionId}")
    public ResponseEntity<Map<String, Object>> getRegionalIntensityByRegionId(@PathVariable String regionId) {
        try {
            Map<String, Object> data = carbonService.getRegionalIntensityByRegionId(regionId);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}