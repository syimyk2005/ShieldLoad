package shield.load.shieldapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimiterProperties {
    private int window;
    private int limit;

    public int getWindow() { return window; }
    public void setWindow(int window) { this.window = window; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}