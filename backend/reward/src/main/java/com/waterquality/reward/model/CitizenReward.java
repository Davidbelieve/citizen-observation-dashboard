package com.waterquality.reward.model;

public class CitizenReward {
    private String citizenId;
    private int totalPoints;
    private String badge;  // Bronze, Silver, Gold, or None
    private int submissionCount;
    
    public String getCitizenId() {
		return citizenId;
	}

	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}

	public int getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	public int getSubmissionCount() {
		return submissionCount;
	}

	public void setSubmissionCount(int submissionCount) {
		this.submissionCount = submissionCount;
	}

	public CitizenReward(String citizenId) {
        this.citizenId = citizenId;
        this.totalPoints = 0;
        this.submissionCount = 0;
        this.badge = "None";
    }
    
    // Auto-generate getters/setters: Right-click → Source → Generate Getters and Setters
}