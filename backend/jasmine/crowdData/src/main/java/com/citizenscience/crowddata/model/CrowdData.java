package com.citizenscience.crowddata.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashMap;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Schema(description = "Data model of CrowdData item")
@Entity
public class CrowdData {
	//fields
	//ideally, the measurements and observations would be two separate classes 
	//with measurement and observation attributes that take the respective class objects
	//i tried this but there was some big errors with entities
	//so its not as object oriented as it could be
	@Schema(description = "Unique indetifier of the CrowdData item", example="1")
	private @Id
	@GeneratedValue Long dataID;
	
	@Schema(description = "ID of the citizen that submitted this CrowdData", example="2")
	private Long citizenID; //mandatory
	
	@Schema(description = "The LocalDateTime when the submission was uploaded", example="2021-05-03T18:15:44.923163")
	private LocalDateTime submissionTime; //for upload
	
	@Schema(description = "The postcode where the reading was taken from", example="SR2 7TZ")
	private String postcode; //mandatory
    
	//wanted to do measurements as a separate class but it wouldn't work with the POST methods :(
	@Schema(description = "The temperature of the water sample in degrees celcius", example="15")
	private double temp;
    
	@Schema(description = "The ph of the water sample", example="7")
	private double pH;
	
	@Schema(description = "The alkalinity of the water sample (mg/L)", example="3")
    private double alkalinity;
	
	@Schema(description = "The turbidity of the water sample (NTU)", example="20")
    private double turbidity;
	
	@Schema(description = "The observed status of the water sample.", example="Can either be: Clear, Cloudy, Murky, Foamy, Oily, Discoloured, Presence of Odour")
    private String observations;
	
	@Schema(description = "String of images", example="true, false, false")
    private boolean[] images = new boolean[3];
	
	@Schema(description = "If the submitted data is valid", example="true")
    private boolean isValid;
    
    public CrowdData() {}
    //constructor 
    
    public CrowdData(Long citizenID, LocalDateTime submissionTime, String postcode, double temp, double pH,
			double alkalinity, double turbidity, String observations, boolean[] images, boolean isValid) {
		super();
		this.citizenID = citizenID;
		this.submissionTime = submissionTime;
		this.postcode = postcode;
		this.temp = temp;
		this.pH = pH;
		this.alkalinity = alkalinity;
		this.turbidity = turbidity;
		this.observations = observations;
		this.images = images;
		this.isValid = isValid;
	}
    
    public Long getDataID() {
		return dataID;
	}


	public void setDataID(Long dataID) {
		this.dataID = dataID;
	}


	public Long getCitizenID() {
		return citizenID;
	}


	public void setCitizenID(Long citizenID) {
		this.citizenID = citizenID;
	}


	public LocalDateTime getSubmissionTime() {
		return submissionTime;
	}

	public void setSubmissionTime(LocalDateTime submissionTime) {
		this.submissionTime = submissionTime;
	}


	public String getPostcode() {
		return postcode;
	}


	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}


	public double getTemp() {
		return temp;
	}


	public void setTemp(double temp) {
		this.temp = temp;
	}


	public double getpH() {
		return pH;
	}


	public void setpH(double pH) {
		this.pH = pH;
	}


	public double getAlkalinity() {
		return alkalinity;
	}


	public void setAlkalinity(double alkalinity) {
		this.alkalinity = alkalinity;
	}


	public double getTurbidity() {
		return turbidity;
	}


	public void setTurbidity(double turbidity) {
		this.turbidity = turbidity;
	}


	public String getObservations() {
		return observations;
	}


	public void setObservations(String observations) {
		this.observations = observations;
	}


	public boolean[] getImages() {
		return images;
	}


	public void setImages(boolean[] images) {
		this.images = images;
	}


	public boolean getIsValid() {
		return isValid;
	}


	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	


	//VALIDATION: user submitted data (usd) must have post code, at least one measurement or observation
	//check postcode first as if statement. no postcode, DEATH
	//then check measurement, no measurement, check observation
	//if measurement, don't check observations 
	//none of both, DEATH
	//isValid value is checked when POST is used
	//also for observations i tried to check each item to make sure it was what was specificed in the brief
	//but it didn't work :(
	public void validation() {
		//checking 4 postcode
		if (postcode == null || postcode == "") {
			//no postcode? INVALID
			isValid = false;
		} else {
			//postcode is valid so moving on to other checks
			//checking measurements first
			if (temp != 0.0 || pH != 0.0 || alkalinity != 0.0 || turbidity != 0.0) {
				//measurements is valid so no need to check observations
				//as just having a valid measurement is enough to make usd valid
				isValid = true;
			} else {
				//measurement is invalid BUT if observations are valid then usd will still be valid
				if (observations != null) {
					//observations is valid so usd is valid
					isValid = true;
				} else {
					//both measurements and observations validation has failed
					//so overall usd is invalid
					isValid = false;
				}
			}
		}
	}
	
	//this makes a string for the message
	//since message is a string and the rewards service can't handle csData objects, conversion is needed
	//not all of the attributes are needed
	//only citizenID and if the submission/record is complete are needed
	public String messageBuilder() {
		//the comma is necessary for some string splitting in the message handling
		//citizenID is necessary for identify the citizen's reward data
		String message = citizenID + ",";
		//this checks if the record is complete or not since this impacts rewards
		if (temp != 0.0 && pH != 0.0 && alkalinity != 0.0 && turbidity != 0.0 && observations != null && images != null) {
			message = message + "Complete Record";
		} else {
			message = message + "Incomplete Record";
			}
			return message;
	}
}
