        package com.waterQualityMonitoring.reward.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reward API")
                        .version("v1")
                        .description("RESTful API for calculating and managing citizen rewards in the Water Quality Monitoring System"));
    }
}

