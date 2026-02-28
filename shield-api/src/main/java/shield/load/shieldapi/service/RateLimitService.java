package shield.load.shieldapi.service;


import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import shield.load.shieldapi.config.RateLimiterProperties;

import java.util.List;


@Service
public class RateLimitService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<Long> script;
    private final RateLimiterProperties props;


    public RateLimitService(
            ReactiveRedisTemplate<String, String> redisTemplate,
            DefaultRedisScript<Long> script,
            RateLimiterProperties props) {
        this.redisTemplate = redisTemplate;
        this.script = script;
        this.props = props;
    }

    public Mono<Boolean> isAllowed(String key) {
        return redisTemplate
                .execute(script,
                        List.of(key),
                        String.valueOf(props.getWindow()),
                        String.valueOf(props.getLimit()))
                .next()
                .map(result -> result == 1);
    }
}