package com.waterQualityMonitoring.gateway;

import java.time.Instant;
import java.util.Map;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

/**
 * Custom error handler that renders friendly JSON responses when a route cannot
 * be located by Spring Cloud Gateway.
 */
@Component
@Order(-2)
public class GatewayErrorHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        if (ex instanceof org.springframework.cloud.gateway.support.NotFoundException
                || (ex instanceof ResponseStatusException rse && rse.getStatusCode().equals(HttpStatus.NOT_FOUND))) {
            return renderNotFoundResponse(exchange);
        }

        return Mono.error(ex);
    }

    private Mono<Void> renderNotFoundResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "message", "Route not found",
                "path", exchange.getRequest().getPath().value(),
                "timestamp", Instant.now().toString());

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        try {
            byte[] responseBytes = objectMapper.writeValueAsBytes(body);
            DataBuffer buffer = bufferFactory.wrap(responseBytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException jsonProcessingException) {
            return Mono.error(jsonProcessingException);
        }
    }
}

