package com.waterQualityMonitoring.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties that provide downstream service URIs to the gateway.
 */
@Configuration
@ConfigurationProperties(prefix="app")
public class UriConfiguration {

	private String crowdSourcedDataApi = "http://localhost:8091";
	private String rewardServiceApi = "http://localhost:8092";
	
	
	/**
	 * @return base URI for the crowdsourced data service
	 */
	public String getCrowdSourcedDataApi() {
		return crowdSourcedDataApi;
	}

	public void setCrowdSourcedDataApi(String crowdSourcedDataApi) {
		this.crowdSourcedDataApi = crowdSourcedDataApi;
	}
	
	/**
	 * @return base URI for the reward service
	 */
	public String getRewardServiceApi() {
		return rewardServiceApi;
	}

	public void setRewardServiceApi(String rewardServiceApi) {
		this.rewardServiceApi = rewardServiceApi;
	}

}
