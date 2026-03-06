package shield.load.shieldapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimiterProperties {
    private int window = 60;
    private int limit = 100;
    private Map<String, EndpointConfig> endpoints = new HashMap<>();

    public EndpointConfig getConfigForPath(String path) {
        String normalized = path.replaceAll("^/", "");
        return endpoints.get(normalized);
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Map<String, EndpointConfig> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(Map<String, EndpointConfig> endpoints) {
        this.endpoints = endpoints;
    }

    public static class EndpointConfig {
        private int limit;
        private int window;

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getWindow() {
            return window;
        }

        public void setWindow(int window) {
            this.window = window;
        }
    }
}