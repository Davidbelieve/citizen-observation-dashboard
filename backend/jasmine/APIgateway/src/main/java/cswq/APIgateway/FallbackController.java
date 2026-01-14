package cswq.APIgateway;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/")
    public Map<String, Object> todosFallback(ServerHttpRequest request) {
        return Map.of(
            "message", "Service temporarily unavailable.",
            "path", request.getPath().toString(),
            "timestamp", Instant.now().toString()
        );
    }
}
