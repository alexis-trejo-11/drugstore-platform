package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class JWTSessions implements Serializable {
  private static final long serialVersionUID = 1L;

  private String userId;
  private String refreshToken;
  private LocalDateTime createdAt;
  private LocalDateTime lastRefreshAt;
  private LocalDateTime expiresAt;
  private long refreshCount;
  private String deviceInfo;
  private String ipAddress;

  public JWTSessions(Token refreshToken, String userId) {
    this.refreshToken = refreshToken.code();
    this.userId = userId;
    this.createdAt = LocalDateTime.now();
    this.expiresAt = refreshToken.expiresAt();
    this.lastRefreshAt = this.createdAt;
    this.refreshCount = 0;
  }

	public static JWTSessions from(
			Token refreshToken,
			String deviceInfo,
			String ipAddress,
			String userId
	) {
		JWTSessions session = new JWTSessions(refreshToken, userId);
		session.setDeviceInfo(deviceInfo);
		session.setIpAddress(ipAddress);
		return session;
	}

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }

  public void refresh() {
    this.lastRefreshAt = LocalDateTime.now();
    this.refreshCount++;
  }

}
