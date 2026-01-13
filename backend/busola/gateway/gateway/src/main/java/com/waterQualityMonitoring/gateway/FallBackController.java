package com.waterQualityMonitoring.gateway;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides fallback responses when downstream services are unavailable.
 */
@RestController
public class FallBackController {

    /**
     * Fallback response for the crowdsourced data service.
     *
     * @param request original request routed through the gateway
     * @return descriptive message for clients
     */
    @RequestMapping("/fallback/observation")
    public Map<String, Object> customerFallback(ServerHttpRequest request) {
        return Map.of(
                "message", "Crowdsourced Data Service is temporarily unavailable.",
                "path", request.getPath().toString(),
                "timestamp", Instant.now().toString());
    }

    /**
     * Fallback response for the reward service.
     */
    @RequestMapping("/fallback/reward")
    public Map<String, Object> trackingFallback(ServerHttpRequest request) {
        return Map.of(
                "message", "Reward Service is temporarily unavailable.",
                "path", request.getPath().toString(),
                "timestamp", Instant.now().toString());
    }

    /**
     * General-purpose fallback for other API routes.
     */
    @RequestMapping("/fallback/api")
    public Map<String, Object> genericFallback(ServerHttpRequest request) {
        return Map.of(
                "message", "Citizen Science water quality monitoring service temporarily unavailable.",
                "path", request.getPath().toString(),
                "timestamp", Instant.now().toString());
    }

}

