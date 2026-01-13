package com.citizenscience.crowddata.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

//config file for activeMQ messaging
@Configuration
@EnableJms
public class ActiveMQConfig {

	//sets broker url to where the activemq broker is running
    private static final String broker = "tcp://localhost:61616";

    //creates a connection factory using the broker url
    //makes sure message is set to the right place
    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(broker);
        return factory;
    }

    //jmsTemplate handles all the messaging business
    //so this constructor sets up a jmsTemplate using the connection factory with the correct broker
    @Bean
    public JmsTemplate jmsTemplate() {
        return new JmsTemplate(connectionFactory());
    }
}