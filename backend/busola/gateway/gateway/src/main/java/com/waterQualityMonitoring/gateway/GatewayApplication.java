package com.waterQualityMonitoring.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Spring Cloud Gateway application that routes traffic to downstream microservices.
 */
@SpringBootApplication
public class GatewayApplication {

    /**
     * Launches the API gateway.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * Registers the route locator configuring paths for crowdsourced and reward services
     * along with fallback behaviour.
     *
     * @param builder          fluent builder to define routes
     * @param uriConfiguration configuration bean supplying downstream URIs
     * @return configured route locator
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {

        String crowdsourcedApi = uriConfiguration.getCrowdSourcedDataApi();
        String rewardApi = uriConfiguration.getRewardServiceApi();

        return builder.routes()
                // Crowdsourced service
                .route("crowdsourced-service", r -> r
                        .path("/api/v1/observations/**")
                        .filters(f -> f
                                .addRequestHeader("Gateway", "Spring Cloud Gateway")
                                .circuitBreaker(c -> c
                                        .setName("crowdsourced-circuit-breaker")
                                        .setFallbackUri("forward:/fallback/observation")))
                        .uri(crowdsourcedApi))

                // Reward service
                .route("reward-service", r -> r
                        .path("/api/v1/rewards/**")
                        .filters(f -> f
                                .addRequestHeader("Gateway", "Spring Cloud Gateway")
                                .circuitBreaker(c -> c
                                        .setName("reward-circuit-breaker")
                                        .setFallbackUri("forward:/fallback/reward")))
                        .uri(rewardApi))

                // Global fallback for remaining APIs (adjust target if needed)
                .route("global-fallback-route", r -> r
                        .path("/api/v1/**")
                        .filters(f -> f.circuitBreaker(c -> c
                                .setName("api-cb")
                                .setFallbackUri("forward:/fallback/api")))
                        .uri(crowdsourcedApi))

                // Actuator route passthrough
                .route("actuator-route", r -> r
                        .path("/api/v1/actuator/**")
                        .filters(f -> f
                                .addRequestHeader("Gateway", "SpringCloudGateway")
                                .circuitBreaker(c -> c
                                        .setName("actuatorCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/api")))
                        .uri(crowdsourcedApi))
                .build();
    }

    /**
     * Configures CORS to allow requests from the frontend application.
     * 
     * @return CORS web filter
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(true);
        corsConfig.addAllowedOriginPattern("http://localhost:*");
        corsConfig.addAllowedOriginPattern("http://127.0.0.1:*");
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedMethod("*");
        corsConfig.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}