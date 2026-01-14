package com.citizenscience.rewards.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.citizenscience.rewards.model.Rewards;
import com.citizenscience.rewards.repository.RewardsRepository;


//this is where most of rewards' functionality comes from
@Component
public class MessageListener {
	
	//sets repository
	//needed to access and update records
	private final RewardsRepository rewardsRepository;
	

    public MessageListener(RewardsRepository rewardsRepository) {
        this.rewardsRepository = rewardsRepository;
   }
    
   //sets queue
   //notice that it's the same as the queue declared in the CrowdData message sender....
   @JmsListener(destination = "crowdSourcedQueue")
   public void listen(String message) {
	   //outputs message to the console
        System.out.println("Received message: " + message);
        //calls the other method in this file so the message can be handled
        MessageHandler(message);
        
   }
    
   //this does all the work
   //checks the message, finds the right record, and updates it
   public void MessageHandler(String message) {
    	System.out.println("Handling message: " + message);
    	//this splits the message based on the comma and stores in an array
		String[] splitMessage = message.split(",");
		//this is the citizen's id
		int id = Integer.parseInt(splitMessage[0]);
		
		//finds the reward record in the repository that matches the id passed by the message
		//should probably have some validation
		//if new citizen, create a new repository item?
		//BUT not sure how to do that soooo maybe in the future
		Rewards rewardRecord = rewardsRepository.findById((long) id).get();
		//testing
		System.out.println("start: " + rewardRecord.getId() + ", " + rewardRecord.getPoints() + ", " + rewardRecord.getAchivementBadge());
		//runs function in the Rewards class that handle the validation
		rewardRecord.updatePoints(splitMessage[1]); //passes in the second part of the message that contains whether the record is complete or not
		rewardRecord.updateBadge();
		//testing
		System.out.println("end: " + rewardRecord.getId() + ", " + rewardRecord.getPoints() + ", " + rewardRecord.getAchivementBadge());
		//saves record to repository
		rewardsRepository.save(rewardRecord);
    }
} 