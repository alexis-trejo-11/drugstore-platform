package io.github.alexisTrejo11.drugstore.users.config.ratelimit;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(prefix = "app.rate-limit.global", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RateLimiterConfig {
}
