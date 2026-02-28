package shield.load.shieldapi.ratelimit;

import org.springframework.stereotype.Component;

import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;
import shield.load.shieldapi.service.RateLimitService;

@Component
public class RateLimitFilter implements WebFilter, Ordered {

    private final RateLimitService service;
    private final RateLimitKeyResolver keyResolver;

    public RateLimitFilter(
            RateLimitService service,
            RateLimitKeyResolver keyResolver) {
        this.service = service;
        this.keyResolver = keyResolver;
    }

    @Override
    public int getOrder() {
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {
        String key = keyResolver.resolve(exchange);

        return service.isAllowed(key)
                .flatMap(allowed -> {
                    if (!allowed) {
                        exchange.getResponse().setRawStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                });
    }
}