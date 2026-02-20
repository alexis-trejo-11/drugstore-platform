package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output;

import java.time.Duration;
import java.util.Optional;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.JWTSessions;

public interface SessionService {
  void blacklistRefreshToken(String refreshToken, Duration duration);

  boolean isBlacklisted(String refreshToken);

  void blackListAllRefreshTokensForUser(String userId);

  void save(JWTSessions session);

  Optional<JWTSessions> findUserJwtSessions(String userId);
}
