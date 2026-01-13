package com.citizenscience.rewards.controller;


import java.time.LocalDateTime;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.citizenscience.rewards.model.Rewards;
import com.citizenscience.rewards.repository.RewardsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/citizenscience")
public class RewardsController {


    private final RewardsRepository rewardsRepository;

    public RewardsController(RewardsRepository rewardsRepository) {
        this.rewardsRepository = rewardsRepository;
   }
    
    //rewards doesn't have any necessary REST calls
    //since everything happens internally through the message listener
    //but I have a get all GET pull purely for testing
    //makes sure that everything is connected ok and that the records are updating
    /**
     * Retrieves a list of Rewards item
     * @return List of Rewards items
     */
    @Operation(summary = "Get All Rewards items", description = "This method retrieves all of Rewards items")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description="Rewards list returns"),
    })    
    @GetMapping("rewards")
    public List<Rewards> all(){
    	return rewardsRepository.findAll();
    }
    
    /**
     * Get Rewards item by id
     * @param id Rewards id
     * @return Rewards item with specified ID
     */
    @Operation(summary = "Get one Rewards by ID", description = "This method retrieves one item of Rewards referenced by the ID")
    @ApiResponses(value = {
    		@ApiResponse(responseCode = "200", description="Reward item is found"),
    		@ApiResponse(responseCode = "404", description="Reward item not found")
    })
    @GetMapping("rewards/{id}")
    public ResponseEntity<Rewards> getRewardById(@PathVariable Long id) {
        return rewardsRepository.findById(id)
                .map(Rewards -> new ResponseEntity<>(Rewards, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    //new endpoints 4 group
    
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("rewards/top3")
    public List<Rewards> getAllLimited() {
    	return rewardsRepository.findAll(
    			  PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "points"))).toList();
    
    }
}
    
    