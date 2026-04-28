package io.github.alexisTrejo11.drugstore.inventories.config.rate_limiter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
@Getter
@Setter
public class RateLimitProperties {
    private GlobalConfig global = new GlobalConfig();
    private DefaultConfig defaultConfig = new DefaultConfig();
    private Map<String, EndpointConfig> endpoints = new HashMap<>();
    
    @Getter
    @Setter
    public static class GlobalConfig {
        private boolean enabled = true;
        private int maxRequests = 1000;
        private int durationHours = 1;
    }
    
    @Getter
    @Setter
    public static class DefaultConfig {
        private int maxRequests = 100;
        private int durationSeconds = 60;
    }
    
    @Getter
    @Setter
    public static class EndpointConfig {
        private int maxRequests;
        private int durationSeconds;
    }
}