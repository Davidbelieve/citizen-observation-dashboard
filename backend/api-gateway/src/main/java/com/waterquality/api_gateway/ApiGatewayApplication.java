package com.waterquality.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			// Route observations endpoints to crowdsourced-data-service (port 8080)
			.route("observations_count", r -> r
				.path("/api/observations/count")
				.uri("http://localhost:8080"))
			.route("observations_recent", r -> r
				.path("/api/observations/recent")
				.uri("http://localhost:8080"))
			.route("observations_submissions", r -> r
				.path("/api/submissions/**")
				.uri("http://localhost:8080"))
			// Route contributors to crowdsourced-data-service
			.route("contributors_leaderboard", r -> r
				.path("/api/contributors/leaderboard")
				.uri("http://localhost:8080"))
			// Route rewards to reward service (port 8081)
			.route("rewards", r -> r
				.path("/rewards/**")
				.uri("http://localhost:8081"))
			.build();
	}
}