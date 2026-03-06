package shield.load.shieldapi.router;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import shield.load.shieldapi.config.RateLimiterProperties;
import shield.load.shieldapi.dto.DecisionRequest;
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

    @PostMapping("/decision")
    public Mono<DecisionResponse> decide(@RequestBody DecisionRequest request) {
        RateLimiterProperties.EndpointConfig config = props.getConfigForPath(request.endpoint());

        if (config == null) {
            return Mono.just(new DecisionResponse("ALLOW", "No limit configured"));
        }

        return rateLimitService.decide(
            request.clientId(),
            request.endpoint(),
            config.getLimit(),
            config.getWindow()
        );
    }
}