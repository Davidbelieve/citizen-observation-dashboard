package com.waterquality.crowdsourced_data.controller;

//Necessary Imports
import com.waterquality.crowdsourced_data.model.WaterQuality;
import com.waterquality.crowdsourced_data.service.WaterQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


//provides endpoints for saving and getting data
@RestController
@RequestMapping("/observations")
public class WaterQualityController {
	private final WaterQualityService service;
	@Autowired
	public WaterQualityController(WaterQualityService service) {
		this.service = service;
	}
	
	//submit a new water quality observation
	@PostMapping
	public ResponseEntity<?> submitObservation(@RequestBody WaterQuality observation){
		try{
			WaterQuality saved = service.submitObservation(observation);
			return new ResponseEntity<>(saved, HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.BAD_REQUEST
					);
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorResponse("Internal server error: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//get all observations
	@GetMapping
	public ResponseEntity<?> getallObservations(){
		try {
			List<WaterQuality>observations = service.getAllObservations();
			return ResponseEntity.ok(observations);	
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ErrorResponse( "Error retrieving observations: " + e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR
					);
		}
		
	}
	//get Observation by ID
	@GetMapping("/{id}")
	public ResponseEntity<?> getObservationById(@PathVariable String id){
		try {
			WaterQuality observation = service.getObservationById(id);
			return ResponseEntity.ok(observation);	
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.NOT_FOUND
					);
		}
		
	}

	//get all the observation of a citizen
	@GetMapping("/citizen/{citizenId}")
	public ResponseEntity<?> getObservationsByCitizen(@PathVariable String citizenId){
		try {
			List<WaterQuality>observations = service.getObservationsByCitizen(citizenId);
			return ResponseEntity.ok(observations);	
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.BAD_REQUEST
					);
		}
		
	}
	//get all the observation of a citizen by postcode
	@GetMapping("/postcode/{postcode}")
	public ResponseEntity<?> getObservationsByPostcode(@PathVariable String postcode){
		try {
			List<WaterQuality> observations = service.getObservationByPostcode(postcode);
			return ResponseEntity.ok(observations);
		}catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.BAD_REQUEST);
		}
	}
	
	//count all the observation of a specific citizen
	@GetMapping("/citizen/{citizenId}/count")
	public ResponseEntity<?> countObservationsByCitizen(@PathVariable String citizenId){
		try {
			long count = service.countObservationsByCitizen(citizenId);
			return ResponseEntity.ok(new CountResponse(count));	
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.BAD_REQUEST
					);
				}
	}
	//Delete an observation by UUID
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteObservation(@PathVariable String id){
		try {
			service.deleteObservation(id);
			return ResponseEntity.noContent().build();
		}catch (IllegalArgumentException e) {
			return new ResponseEntity<>(
					new ErrorResponse(e.getMessage()),
					HttpStatus.NOT_FOUND
					);
		}
		
	}
	//Inner class for error responses
	public static class ErrorResponse{
		private String error;
		
		public ErrorResponse(String error) {
		this.error = error;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
 }
	//Inner class for count responses
	public static class CountResponse{
		private long count;
		
		public CountResponse(long count) {
		this.count = count;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
 }
}
