package cswq.APIgateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class UriConfiguration {
    private String csData = "http://localhost:8095";
    private String rewards = "http://localhost:8096";
    
    public String getCsData() { 
    	return csData; 
    }
    public void setCsData(String csData) { 
    	this.csData = csData; 
    }
    
    public String getRewards() { 
    	return rewards; 
    }
    public void setRewards(String rewards) { 
    	this.rewards = rewards; 
    }
}

