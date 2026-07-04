package io.github.alexisTrejo11.drugstore.address.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * Redis settings for shared instances (e.g. Redis Cloud).
 * All keys written by this service are prefixed to avoid collisions with other services.
 */
@Component
@ConfigurationProperties(prefix = "app.redis")
@Data
public class RedisProperties {

  /**
   * Prefix for every Redis key owned by address-service.
   * Example: drugstore:address:rate_limit:client-ip
   */
  private String keyPrefix = "drugstore:address:";

  public String normalizedPrefix() {
    if (keyPrefix == null || keyPrefix.isBlank()) {
      return "";
    }
    return keyPrefix.endsWith(":") ? keyPrefix : keyPrefix + ":";
  }

  public String prefixKey(String key) {
    if (key == null || key.isBlank()) {
      return normalizedPrefix();
    }
    String prefix = normalizedPrefix();
    if (prefix.isEmpty()) {
      return key;
    }
    return key.startsWith(prefix) ? key : prefix + key;
  }
}
