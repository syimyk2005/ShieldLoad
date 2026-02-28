package shield.load.shieldapi.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import shield.load.shieldapi.config.RateLimiterProperties;
import shield.load.shieldapi.ratelimit.RateLimitKeyResolver;

import java.util.List;
import java.util.Map;

@Component
public class RateLimitStatusService {

    private final StringRedisTemplate redisTemplate;
    private final RateLimiterProperties properties;
    private final RateLimitKeyResolver keyResolver;

    public RateLimitStatusService(StringRedisTemplate redisTemplate,
                                  RateLimiterProperties properties,
                                  RateLimitKeyResolver keyResolver) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.keyResolver = keyResolver;
    }

    public Map<String, Object> getStatus(ServerWebExchange exchange) {
        String key = keyResolver.resolve(exchange);

        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText(
            "local key = KEYS[1]\n" +
            "local window = tonumber(ARGV[1])\n" +
            "local limit = tonumber(ARGV[2])\n" +
            "local current = tonumber(redis.call('GET', key) or '0')\n" +
            "local ttl = redis.call('TTL', key)\n" +
            "local remaining = math.max(limit - current, 0)\n" +
            "if ttl < 0 then ttl = window end\n" +
            "return {current, remaining, ttl}"
        );
        script.setResultType(List.class);

        List<Long> result = redisTemplate.execute(
            script,
            List.of(key),
            String.valueOf(properties.getWindow()),
            String.valueOf(properties.getLimit())
        );

        return Map.of(
            "used", result.get(0),
            "remaining", result.get(1),
            "resetInSeconds", result.get(2)
        );
    }
}