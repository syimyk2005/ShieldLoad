package shield.load.shieldapi.router;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import shield.load.shieldapi.ratelimit.RateLimitKeyResolver;
import shield.load.shieldapi.service.RateLimitStatusService;

import java.util.Map;

@RestController
public class RateLimitStatusController {

    private final RateLimitStatusService statusService;
    private final RateLimitKeyResolver keyResolver;

    public RateLimitStatusController(RateLimitStatusService statusService,
                                     RateLimitKeyResolver keyResolver) {
        this.statusService = statusService;
        this.keyResolver = keyResolver;
    }

    @GetMapping("/rate-limiter/live/status")
    public Mono<Map<String, Object>> getRateLimitStatusForEndpoint(
            ServerWebExchange exchange,
            @RequestParam String endpoint) {
        String identity = keyResolver.resolveIdentity(exchange);
        String key = "rl:" + identity + ":" + endpoint;
        return statusService.getStatus(key);
    }

    @GetMapping("/rate-limiter/live/all")
    public Mono<Map<String, Object>> getAllEndpointsStatus(ServerWebExchange exchange) {
        String identity = keyResolver.resolveIdentity(exchange);
        return statusService.getAllStatus(identity);
    }
}