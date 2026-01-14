package com.citizenscience.crowddata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

//service that actually sends the messages
@Service
public class MessageService {

	//sets the name of the queue
	//the broker can have multiple queues so the right one needs to be selected
    private static final String queue = "crowdSourcedQueue";
    
    //initialising the jmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;

    //method that actually sends message
    //takes in the string that is actually the message contents
    //uses the previously defined queue as the destination
    //message string as message body
    public void sendMessage(String message) {
        jmsTemplate.convertAndSend(queue, message);
    }
}