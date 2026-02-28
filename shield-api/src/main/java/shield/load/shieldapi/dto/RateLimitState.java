package shield.load.shieldapi.dto;

public record RateLimitState(long remaining, long resetSeconds, long limit) {}