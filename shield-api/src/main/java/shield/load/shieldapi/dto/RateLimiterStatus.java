package shield.load.shieldapi.dto;

public record RateLimiterStatus(
        int limit,
        int used,
        int remaining,
        long resetInSeconds,
        int windowSeconds
) {}