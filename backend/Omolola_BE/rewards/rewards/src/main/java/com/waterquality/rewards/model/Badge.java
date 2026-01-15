package com.waterquality.rewards.model;

//Necessary Imports
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

@Embeddable
public class Badge {
	
	@Enumerated(EnumType.STRING)
	private BadgeType type;
	
	private LocalDateTime earnedDate;
	
	//default constructor
	public Badge() {
		
	}
	
	//Constructor with parameters
	public Badge(BadgeType type, LocalDateTime earnedDate) {
		this.type = type;
		this.earnedDate = earnedDate;
	}
	//Getters and Setters

	public BadgeType getType() {
		return type;
	}

	public void setType(BadgeType type) {
		this.type = type;
	}

	public LocalDateTime getEarnedDate() {
		return earnedDate;
	}

	public void setEarnedDate(LocalDateTime earnedDate) {
		this.earnedDate = earnedDate;
	}
	
}
