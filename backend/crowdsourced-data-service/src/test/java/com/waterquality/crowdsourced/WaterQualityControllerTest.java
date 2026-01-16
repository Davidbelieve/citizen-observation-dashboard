package com.waterquality.crowdsourced;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waterquality.crowdsourced.model.WaterQualitySubmission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WaterQualityControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Test 1: Valid complete submission - should return 201 Created
     */
    @Test
    public void testValidCompleteSubmission() throws Exception {
        WaterQualitySubmission submission = new WaterQualitySubmission();
        submission.setPostcode("NE1 8ST");
        submission.setTemperature(15.5);
        submission.setPh(7.2);
        submission.setAlkalinity(120.0);
        submission.setTurbidity(2.5);
        submission.setObservations("Clear");
        
        mockMvc.perform(post("/api/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postcode").value("NE1 8ST"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.timestamp").exists());
        
        System.out.println("✓ Test 1 PASSED: Valid complete submission");
    }
    
    /**
     * Test 2: Valid partial submission (only measurements) - should return 201
     */
    @Test
    public void testValidPartialSubmission() throws Exception {
        WaterQualitySubmission submission = new WaterQualitySubmission();
        submission.setPostcode("L1 2AB");
        submission.setTemperature(14.0);
        
        mockMvc.perform(post("/api/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postcode").value("L1 2AB"));
        
        System.out.println("✓ Test 2 PASSED: Valid partial submission");
    }
    
    /**
     * Test 3: Invalid submission - missing postcode - should return 400
     */
    @Test
    public void testInvalidSubmissionMissingPostcode() throws Exception {
        WaterQualitySubmission submission = new WaterQualitySubmission();
        submission.setTemperature(15.0);
        submission.setPh(7.0);
        
        mockMvc.perform(post("/api/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid submission"));
        
        System.out.println("✓ Test 3 PASSED: Invalid submission rejected");
    }
    
    /**
     * Test 4: Invalid - postcode only, no measurements or observations
     */
    @Test
    public void testInvalidSubmissionPostcodeOnly() throws Exception {
        WaterQualitySubmission submission = new WaterQualitySubmission();
        submission.setPostcode("NE1 8ST");
        
        mockMvc.perform(post("/api/submissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isBadRequest());
        
        System.out.println("✓ Test 4 PASSED: Postcode-only submission rejected");
    }
    
    /**
     * Test 5: GET all submissions - should return 200 OK
     */
    @Test
    public void testGetAllSubmissions() throws Exception {
        mockMvc.perform(get("/api/submissions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        System.out.println("✓ Test 5 PASSED: Get all submissions works");
    }
}

