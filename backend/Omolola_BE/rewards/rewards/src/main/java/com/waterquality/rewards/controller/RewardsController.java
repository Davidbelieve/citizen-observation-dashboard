package com.waterquality.rewards.controller;

//Necessary imports
import com.waterquality.rewards.model.BadgeType;
import com.waterquality.rewards.model.Rewards;
import com.waterquality.rewards.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rewards")
public class RewardsController {
	
	private final RewardsService service;
	
	@Autowired
	public RewardsController(RewardsService service) {
		this.service = service;
	}
	
	//Add points for submission
	@PostMapping("/points")
	public ResponseEntity<?> addPoints(@RequestBody AddPointsRequest request){
		try {
			Rewards reward = service.addPointsForSubmission(
					request.getCitizenId(),
					request.isComplete()
					);
			return ResponseEntity.ok(reward);
		}catch(IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.BAD_REQUEST
					);
		}
	}
	
	//Get reward for a citizen
	@GetMapping("/citizen/{citizenId}")
	public ResponseEntity<?> getRewardByCitizen(@PathVariable String citizenId) {
		try {
			Optional<Rewards> reward = service.getRewardsByCitizen(citizenId);
			if(reward.isPresent()) {
				return ResponseEntity.ok(reward.get());
			}else {
				return new ResponseEntity<>(
						new ErrorResponse("No rewards found for citizen:" + citizenId),
						HttpStatus.NOT_FOUND
						);
			}
		}catch(IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.BAD_REQUEST
					);
		}
	}

	//Get all rewards
	@GetMapping
	public ResponseEntity<?> getAllRewards() {
		try {
			List<Rewards> rewards = service.getAllRewards();
			return ResponseEntity.ok(rewards);
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorResponse("Error retrieving rewards: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR
					);
		}
	}
	
	//Get leaderboard
	@GetMapping("/leaderboard")
	public ResponseEntity<?> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
		try {
			List<Rewards> leaderboard = service.getLeaderboard(limit);
			return ResponseEntity.ok(leaderboard);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.BAD_REQUEST
					);
		}
	}
	
	//Get citizens by badge type
	@GetMapping("/badge/{badgeType}")
	public ResponseEntity<?> getCitizensByBadge(@PathVariable String badgeType){
		try {
			BadgeType type = BadgeType.valueOf(badgeType.toUpperCase());
			List<Rewards> citizens = service.getCitizensByBadge(type);
			return ResponseEntity.ok(citizens);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse("Invalid badge type. Must be Bronze, Silver, or Gold"),
					HttpStatus.BAD_REQUEST
					);
		}
	}
	
	//POST - Calculate rewards for all citizens
	@PostMapping("/calculate")
	public ResponseEntity<?> calculateAllRewards() {
	    try {
	        // This will trigger the service to fetch observations and calculate rewards
	        service.calculateRewardsForAllCitizens();
	        return ResponseEntity.ok(new SuccessResponse("Rewards calculated successfully"));
	    } catch (Exception e) {
	        return new ResponseEntity<>(
	            new ErrorResponse("Error calculating rewards: " + e.getMessage()),
	            HttpStatus.INTERNAL_SERVER_ERROR
	        );
	    }
	}

	// Success Response class 
	public static class SuccessResponse {
	    private String message;
	    
	    public SuccessResponse(String message) {
	        this.message = message;
	    }
	    
	    public String getMessage() {
	        return message;
	    }
	    
	    public void setMessage(String message) {
	        this.message = message;
	    }
	}
	//class for add points request
	public static class AddPointsRequest{
		private String citizenId;
		private boolean complete;
		
		public AddPointsRequest() {	
		}
		
		public AddPointsRequest(String citizenId, boolean complete) {
			this.citizenId = citizenId;
			this.complete = complete;
		}
		
		public String getCitizenId() {
			return citizenId;
		}
		
		public void setCitizenId(String citizenId) {
			this.citizenId = citizenId;
		}
		
		public boolean isComplete() {
			return complete;
		}
		
		public void setComplete(boolean complete) {
			this.complete = complete;
		}
	}
	
	// class  for error responses
	
	public static class ErrorResponse{
		private String error;
		
		public ErrorResponse(String error) {
			this.error = error;
		}
		
		public String getError() {
			return error;
		}
		
		public void setError(String error ) {
			this.error = error;
		}
	}

}
