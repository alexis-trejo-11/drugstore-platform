package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output;

import java.util.Optional;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.models.JWTSessions;

public interface SessionRepository {
  void blacklistRefreshToken(String refreshToken);

  boolean isBlacklisted(String refreshToken);

  void blackListAllRefreshTokensForUser(String userId);

  void save(JWTSessions session);

  Optional<JWTSessions> findUserJwtSessions(String userId);
}
