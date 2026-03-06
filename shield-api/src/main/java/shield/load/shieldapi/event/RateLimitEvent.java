package shield.load.shieldapi.event;


public record RateLimitEvent(String clientId, String endpoint, long timestamp) {

}

