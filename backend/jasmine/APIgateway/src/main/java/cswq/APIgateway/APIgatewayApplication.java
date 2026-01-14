package cswq.APIgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;


@SpringBootApplication
@Configuration
@EnableConfigurationProperties(UriConfiguration.class)
@RestController
public class APIgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(APIgatewayApplication.class, args);
	}
	
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
        String csData = uriConfiguration.getCsData(); 
        String rewards = uriConfiguration.getRewards();

        // Simple Route
        //return builder.routes().build();
        
        return builder.routes()
            // Route 1: /crowd/** → local crowdData
            .route(p -> p
                .path("/citizenscience/crowd/**")
                .filters(f -> f
                    .addRequestHeader("Gateway", "SpringCloudGateway"))
                .uri(csData))
            
            // Route 2: host-based route with circuit breaker fallback
            .route(p -> p
                .host("*.csdata.circuitbreaker.com")
                .filters(f -> f.circuitBreaker(config -> config
                    .setName("csdata-cb")
                    .setFallbackUri("forward:/fallback/")))
                .uri(csData))
            //route 3: /rewards/** -> local rewards
            .route(p -> p
                    .path("/citizenscience/rewards/**")
                    .filters(f -> f
                        .addRequestHeader("Gateway", "SpringCloudGateway"))
                    .uri(rewards))
            //route 4: host-based route with circuit breaker fallback
            .route(p -> p
                    .host("*.rewards.circuitbreaker.com")
                    .filters(f -> f.circuitBreaker(config -> config
                        .setName("rewards-cb")
                        .setFallbackUri("forward:/fallback")))
                    .uri(rewards))
            
            .build();
    }

    @Bean
    public FallbackController fallbackController() {
        return new FallbackController();
    }

}

