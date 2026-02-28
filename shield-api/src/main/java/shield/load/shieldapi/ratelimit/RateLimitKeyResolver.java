package shield.load.shieldapi.ratelimit;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class RateLimitKeyResolver {

    public String resolve(ServerWebExchange exchange) {
        String path = normalizePath(exchange.getRequest().getPath().value());
        String identity = resolveIdentity(exchange);
        return "rl:" + identity + ":" + path;
    }

    public String resolveIdentity(ServerWebExchange exchange) {
        String apiKey = exchange.getRequest().getHeaders().getFirst("X-API-KEY");
        if (apiKey != null) {
            return "api:" + apiKey;
        }
        var remote = exchange.getRequest().getRemoteAddress();
        if (remote == null || remote.getAddress() == null) {
            return "unknown";
        }
        return "ip:" + remote.getAddress().getHostAddress();
    }
    private String normalizePath(String path) {
        return path
                .replaceAll("/[0-9a-fA-F]{8}-[0-9a-fA-F-]{27}", "/{uuid}")
                .replaceAll("/\\d+", "/{id}")
                .replaceAll("/$", "");
    }
}