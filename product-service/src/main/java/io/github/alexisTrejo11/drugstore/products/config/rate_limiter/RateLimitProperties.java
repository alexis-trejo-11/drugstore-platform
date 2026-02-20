package io.github.alexisTrejo11.drugstore.products.config.rate_limiter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {
  private GlobalConfig global = new GlobalConfig();
  private Map<String, ProfileConfig> profiles = new HashMap<>();

  public static class ProfileConfig {
    private int maxRequests;
    private int durationSeconds;

    public int getMaxRequests() {
      return maxRequests;
    }

    public int getDurationSeconds() {
      return durationSeconds;
    }

    public void setMaxRequests(int maxRequests) {
      this.maxRequests = maxRequests;
    }

    public void setDurationSeconds(int durationSeconds) {
      this.durationSeconds = durationSeconds;
    }
  }

  public static class GlobalConfig {
    private boolean enabled = true;
    private int maxRequests = 1000;
    private int durationHours = 1;

    public boolean isEnabled() {
      return enabled;
    }

    public int getMaxRequests() {
      return maxRequests;
    }

    public int getDurationHours() {
      return durationHours;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public void setMaxRequests(int maxRequests) {
      this.maxRequests = maxRequests;
    }

    public void setDurationHours(int durationHours) {
      this.durationHours = durationHours;
    }
  }

  public GlobalConfig getGlobal() {
    return global;
  }

  public void setGlobal(GlobalConfig global) {
    this.global = global;
  }

  public Map<String, ProfileConfig> getProfiles() {
    return profiles;
  }

  public void setProfiles(Map<String, ProfileConfig> profiles) {
    this.profiles = profiles;
  }
}
