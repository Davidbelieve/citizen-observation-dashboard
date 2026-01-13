package com.workshop03.APIgateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.tomakehurst.wiremock.http.Fault;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        // Point the gateway’s UriConfiguration at WireMock
        "app.todo-api=http://localhost:${wiremock.server.port}"
    }
)
@AutoConfigureWireMock(port = 0)
class APIgatewayApplicationTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    void routesTodoThroughGateway() {
        // Downstream stub: since the gateway does NOT strip prefix,
        // it will call /todo/1 on the backend.
        stubFor(get(urlEqualTo("/todo/1"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":1,\"title\":\"Learn Spring Boot\",\"completed\":false}")));

        webClient.get().uri("/todo/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.title").isEqualTo("Learn Spring Boot")
            .jsonPath("$.completed").isEqualTo(false);
    }

	/*
	 * @Test void circuitBreakerFallsBackOnBackendFailure() { // Host-based route:
	 * *.todo.circuitbreaker.com // Simulate a network failure so CircuitBreaker
	 * triggers fallback. stubFor(get(urlEqualTo("/todo/fail"))
	 * .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));
	 * 
	 * webClient.get().uri("/todo/fail") .header("Host",
	 * "api.todo.circuitbreaker.com") .exchange() .expectStatus().isOk()
	 * .expectBody() .consumeWith(resp -> assertThat(new
	 * String(resp.getResponseBody())) .contains("temporarily") // matches
	 * FallbackController message ); }
	 */
    
}
