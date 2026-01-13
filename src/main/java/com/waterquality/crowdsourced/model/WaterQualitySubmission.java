

package com.waterquality.crowdsourced.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "water_quality_submissions")
public class WaterQualitySubmission {
    
	 @Id
    private UUID id;
    private LocalDateTime timestamp;
    private String citizenId;
    
    // Mandatory field
    private String postcode;
    
    // Optional measurements
    private Double temperature;  // Celsius
    private Double ph;
    private Double alkalinity;   // mg/L
    private Double turbidity;    // NTU
    
    // Optional observations (could use enum later)
    private String observations;  // e.g., "Clear, Cloudy"
    
    // Optional images (storing as filenames/URLs for now)
    private String image1;
    private String image2;
    private String image3;
    
    
    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getCitizenId() {
		return citizenId;
	}

	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getPh() {
		return ph;
	}

	public void setPh(Double ph) {
		this.ph = ph;
	}

	public Double getAlkalinity() {
		return alkalinity;
	}

	public void setAlkalinity(Double alkalinity) {
		this.alkalinity = alkalinity;
	}

	public Double getTurbidity() {
		return turbidity;
	}

	public void setTurbidity(Double turbidity) {
		this.turbidity = turbidity;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	public String getImage3() {
		return image3;
	}

	public void setImage3(String image3) {
		this.image3 = image3;
	}

	// Default constructor
    public WaterQualitySubmission() {
        this.id = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
    }
    
    
}