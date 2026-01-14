package com.citizenscience.crowddata.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.citizenscience.crowddata.service.MessageService;

import com.citizenscience.crowddata.model.CrowdData;
import com.citizenscience.crowddata.repository.CrowdDataRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/citizenscience")
public class CrowdDataController {

    
    @Autowired
    private MessageService messageService;


    private final CrowdDataRepository crowdDataRepository;

    public CrowdDataController(CrowdDataRepository crowdDataRepository) {
        this.crowdDataRepository = crowdDataRepository;
    }
    
    
    /**
     * *Retrieves a list of CrowdData items
     * @return List of CrowdData
     */
   /* @Operation(summary = "Get all CrowdData items", description = "This method retrieves a list of all the CrowdData items stored in the database")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description="List of CrowdData items successfuly retrieved")
    })*/
    
    @GetMapping("crowd")
    public List<CrowdData> all(){
    	return crowdDataRepository.findAll();
    }

    /**
     * Retrieves a CrowdData item by id
     * @param id CrowdData ID
     * @return CrowdData item with the specified ID
     */
    @Operation(summary = "Get one CrowdData by ID", description = "This method retrieves one item of CrowdData stored in the database referenced by the ID")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description="CrowdData item is found"),
    		@ApiResponse(responseCode = "404", description="CrowdData item not found")
    })
    
    @GetMapping("crowd/{id}")
    public ResponseEntity<CrowdData> getCrowdDataById(@PathVariable Long id) {
        return crowdDataRepository.findById(id)
                .map(csData -> new ResponseEntity<>(csData, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Creates a new CrowdData item 
     * @param newCrowdData
     * @return new CrowdData item
     */
    @Operation(summary = "Create new CrowdData item", description = "Creates a new CrowdData item based on user input")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description="CrowdData item is found"),
    		@ApiResponse(responseCode = "406", description="User submission is invalid")
    })
    
    @PostMapping("crowd")
    public ResponseEntity<CrowdData> createNewCrowdData(@RequestBody CrowdData newCrowdData){
    	//setting the submission time
    	//normally this would be passed in from the front end
    	//so REMOVE THIS BIT WHEN FRONT END IS SORTED
    	newCrowdData.setSubmissionTime(LocalDateTime.now());
    	//validate before creating new customer
    	newCrowdData.validation();
    	if (newCrowdData.getIsValid() == true) {
    		String message = newCrowdData.messageBuilder();
    		CrowdData newItem = crowdDataRepository.save(newCrowdData);
    		messageService.sendMessage(message);
            System.out.println("message sent: " + message);
        	return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    	} else {
    		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    	}
    }
    
    //new endpoints for group
    
    @GetMapping("crowd/count")
    public long getCount() {
    	return crowdDataRepository.count();
    }
    
    @GetMapping("crowd/top5")
    public List<CrowdData> getAllLimited() {
    	return crowdDataRepository.findAll(
  			  PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "submissionTime"))).toList();
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        Optional<CrowdData> crowdData = this.crowdDataRepository.findById(id);
        if(crowdData.isPresent()){
            this.crowdDataRepository.delete(crowdData.get());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}