package shield.load.shieldapi.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import shield.load.shieldapi.config.RateLimiterProperties;

import java.util.List;
import java.util.Map;

@Component
public class RateLimitStatusService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final RateLimiterProperties properties;

    public RateLimitStatusService(ReactiveRedisTemplate<String, String> redisTemplate,
                                  RateLimiterProperties properties) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
    }

    private static final String STATUS_SCRIPT =
            "local key = KEYS[1]\n" +
                    "local window = tonumber(ARGV[1])\n" +
                    "local limit = tonumber(ARGV[2])\n" +
                    "local current = tonumber(redis.call('GET', key) or '0')\n" +
                    "local ttl = redis.call('TTL', key)\n" +
                    "local remaining = math.max(limit - current, 0)\n" +
                    "if ttl < 0 then ttl = window end\n" +
                    "return {current, remaining, ttl}";

    public Mono<Map<String, Object>> getStatus(String key) {
        String path = key.substring(key.indexOf("/", key.indexOf("rl:"))).replaceAll("^/", "");
        RateLimiterProperties.EndpointConfig config = properties.getConfigForPath(path);
        int limit  = config != null ? config.getLimit()  : properties.getLimit();
        int window = config != null ? config.getWindow() : properties.getWindow();

        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptText(STATUS_SCRIPT);
        script.setResultType(List.class);

        return redisTemplate.execute(script, List.of(key),
                        String.valueOf(window), String.valueOf(limit))
                .next()
                .map(result -> {
                    List<Long> r = (List<Long>) result;
                    return Map.of(
                            "endpoint",       path,
                            "used",           r.get(0),
                            "remaining",      r.get(1),
                            "resetInSeconds", r.get(2),
                            "limit",          limit,
                            "window",         window
                    );
                });
    }

    public Mono<Map<String, Object>> getAllStatus(String identity) {
        String pattern = "rl:" + identity + ":*";

        return redisTemplate.keys(pattern)
                .flatMap(key -> getStatus(key))
                .collectMap(
                        m -> (String) m.get("endpoint"),
                        m -> m
                )
                .map(result -> Map.of("client", identity, "endpoints", result));
    }
}