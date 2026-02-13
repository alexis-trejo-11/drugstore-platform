package microservice.auth.app.auth.core.domain.models;

import lombok.Getter;
import lombok.Setter;
import microservice.auth.app.auth.core.domain.valueobjects.UserId;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class JWTSessions {
  private UserId userId;
  private String refreshToken;
  private LocalDateTime createdAt;
  private LocalDateTime lastRefreshAt;
  private long refreshCount;

  public JWTSessions(String refreshToken, UserId userId, Duration duration) {
    this.refreshToken = refreshToken;
    this.userId = userId;
    this.createdAt = LocalDateTime.now();
    this.lastRefreshAt = this.createdAt.plus(duration);
    this.refreshCount = 0;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(lastRefreshAt);
  }
}
