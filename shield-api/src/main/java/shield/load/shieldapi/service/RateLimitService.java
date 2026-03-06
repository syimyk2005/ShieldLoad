package shield.load.shieldapi.service;


import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import shield.load.shieldapi.dto.DecisionResponse;

import java.time.Duration;
import java.util.List;


@Service
public class RateLimitService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<Long> script;


    public RateLimitService(
            ReactiveRedisTemplate<String, String> redisTemplate,
            DefaultRedisScript<Long> script) {
        this.redisTemplate = redisTemplate;
        this.script = script;
    }

    public Mono<Boolean> isAllowed(String key, int limit, int window) {
        return redisTemplate
                .execute(script,
                        List.of(key),
                        String.valueOf(window),
                        String.valueOf(limit))
                .next()
                .map(result -> result == 1);
    }

    public Mono<DecisionResponse> decide(String clientId, String endpoint, int limit, int window) {
        String key = "rate:" + clientId + ":" + endpoint;
        String exceedKey = key + ":exceed";

        return redisTemplate
                .execute(script, List.of(key), String.valueOf(window), String.valueOf(limit))
                .next()
                .flatMap(result -> {
                    if (result == 1) {
                        return redisTemplate.delete(exceedKey)
                                .thenReturn(new DecisionResponse("ALLOW", "Within rate limit"));
                    }

                    return redisTemplate.opsForValue()
                            .increment(exceedKey)
                            .flatMap(exceedCount -> {
                                if (exceedCount == 1) {
                                    return redisTemplate.expire(exceedKey, Duration.ofSeconds(window))
                                            .thenReturn(new DecisionResponse("LIMIT", "Rate limit exceeded"));
                                }
                                return Mono.just(new DecisionResponse("BLOCK", "Repeated violations: " + exceedCount));
                            });
                });
    }

}