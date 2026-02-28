package shield.load.shieldapi.ratelimit;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class RateLimitKeyResolver {

    public String resolve(ServerWebExchange exchange) {
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
        if (apiKey != null) {
            return "rl:api:" + apiKey;
        }

        var remote = exchange.getRequest().getRemoteAddress();
        if (remote == null || remote.getAddress() == null) {
            return "rl:unknown";
        }

        String ip = remote.getAddress().getHostAddress();
        return "rl:ip:" + ip;
    }
}