package io.github.alexisTrejo11.drugstore.accounts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.redis")
@Data
public class RedisProperties {

  private String keyPrefix = "drugstore:auth:";

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
