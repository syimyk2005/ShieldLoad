package shield.load.shieldapi.router;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import shield.load.shieldapi.config.RateLimiterProperties;
import shield.load.shieldapi.dto.DecisionResponse;
import shield.load.shieldapi.service.RateLimitService;

@RestController
public class DecisionController {

    private final RateLimitService rateLimitService;
    private final RateLimiterProperties props;

    public DecisionController(RateLimitService rateLimitService, RateLimiterProperties props) {
        this.rateLimitService = rateLimitService;
        this.props = props;
    }

    @GetMapping("/decision")
    public Mono<ResponseEntity<DecisionResponse>> decide(
            @RequestHeader("X-Client-IP") String clientId,
            @RequestHeader("X-Endpoint") String endpoint
    ) {
        RateLimiterProperties.EndpointConfig config = props.getConfigForPath(endpoint);

        if (config == null) {
            return Mono.just(ResponseEntity.ok(new DecisionResponse("ALLOW", "No limit configured")));
        }

        return rateLimitService.decide(clientId, endpoint, config.getLimit(), config.getWindow())
                .map(response -> switch (response.decision()) {
                    case "ALLOW" -> ResponseEntity.ok(response);
                    case "LIMIT" -> ResponseEntity.status(403).body(response);
                    case "BLOCK" -> ResponseEntity.status(403).body(response);
                    default -> ResponseEntity.ok(response);
                });
    }
}