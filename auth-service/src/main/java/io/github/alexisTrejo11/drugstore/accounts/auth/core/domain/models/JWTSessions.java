package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserId;

@Getter
@Setter
@NoArgsConstructor
public class JWTSessions implements Serializable {
  private static final long serialVersionUID = 1L;

  private UserId userId;
  private String refreshToken;
  private LocalDateTime createdAt;
  private LocalDateTime lastRefreshAt;
  private LocalDateTime expiresAt;
  private long refreshCount;
  private String deviceInfo;
  private String ipAddress;

  public JWTSessions(String refreshToken, UserId userId, Duration duration) {
    this.refreshToken = refreshToken;
    this.userId = userId;
    this.createdAt = LocalDateTime.now();
    this.expiresAt = this.createdAt.plus(duration);
    this.lastRefreshAt = this.createdAt;
    this.refreshCount = 0;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  public void refresh() {
    this.lastRefreshAt = LocalDateTime.now();
    this.refreshCount++;
  }

  public String getUserIdValue() {
    return userId != null ? userId.value() : null;
  }
}
