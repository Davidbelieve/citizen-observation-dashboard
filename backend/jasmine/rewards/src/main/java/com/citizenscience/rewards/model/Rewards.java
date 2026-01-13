package com.citizenscience.rewards.model;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Schema(description = "Data model of Rewards item")
@Entity
public class Rewards {
	
	@Schema(description = "Unique identifer of Rewards item", example="1")
    private @Id
    @GeneratedValue Long id;
	
	@Schema(description = "The ID of the user that the rewards record belongs to", example="3")
    private Long customerID;
	
	@Schema(description = "The points the user has", example="200")
    private int points;
	
	@Schema(description = "The badge the user has", example="Silver")
    private String achivementBadge;
	
	@Schema(description = "If the record has been set", example="true")
    private boolean isSet;

    // Constructors
    public Rewards() {}

	public Rewards(Long customerID, int points, String achivementBadge, boolean isSet) {
		this.customerID = customerID;
		this.points = points;
		this.achivementBadge = achivementBadge;
		this.isSet = isSet;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerID() {
		return customerID;
	}

	public void setCustomerID(Long customerID) {
		this.customerID = customerID;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getAchivementBadge() {
		return achivementBadge;
	}

	public void setAchivementBadge(String achivementBadge) {
		this.achivementBadge = achivementBadge;
	}

	public boolean isSet() {
		return isSet;
	}

	public void setSet(boolean isSet) {
		this.isSet = isSet;
	}
	
	//checks if record is complete based on what is sent
	//only two possible options since the message is harcoded in: 'Complete Record' or 'Incomplete Record' 
	//so if and else used
	//complete record? 20 points added
	//incomplete? 10 points
	public void updatePoints(String recordStatus) {
		   if (recordStatus.contains("Complete Record")) {
			   points = points + 20;
		   } else{
			   points = points + 10;
		   }
	}
	
	//checks if the points that the citizen has are within the thresholds for each badge
	public void updateBadge() {
		if (points >= 100 && points < 200) {
			setAchivementBadge("Bronze");
		} else if (points >= 200 && points < 500) {
			setAchivementBadge("Silver");
		} else if (points > 500) {
			setAchivementBadge("Gold");
		}
	}
}
