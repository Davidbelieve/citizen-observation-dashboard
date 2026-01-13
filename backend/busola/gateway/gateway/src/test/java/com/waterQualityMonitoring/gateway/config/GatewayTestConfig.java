package com.waterQualityMonitoring.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;

import com.waterQualityMonitoring.gateway.UriConfiguration;

/**
 * Overrides URI configuration during tests to point to mock servers.
 */
@TestConfiguration
public class GatewayTestConfig {

    /**
     * Provides a test-specific {@link UriConfiguration} backed by MockWebServer
     * URLs.
     */
    @Bean
    @Primary
    public UriConfiguration testUriConfiguration(
            @Value("${test.crowdsourced.baseUrl}") String crowdsourcedBaseUrl,
            @Value("${test.reward.baseUrl}") String rewardBaseUrl) {
        UriConfiguration configuration = new UriConfiguration();
        configuration.setCrowdSourcedDataApi(crowdsourcedBaseUrl);
        configuration.setRewardServiceApi(rewardBaseUrl);
        return configuration;
    }
}

