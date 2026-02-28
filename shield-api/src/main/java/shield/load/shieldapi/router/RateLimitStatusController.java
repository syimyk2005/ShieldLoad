package shield.load.shieldapi.router;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import shield.load.shieldapi.service.RateLimitStatusService;

import java.util.Map;

@RestController
public class RateLimitStatusController {

    private final RateLimitStatusService statusService;

    public RateLimitStatusController(RateLimitStatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/rate-limiter/live")
    public Map<String, Object> getRateLimitStatus(ServerWebExchange exchange) {
        return statusService.getStatus(exchange);
    }
}