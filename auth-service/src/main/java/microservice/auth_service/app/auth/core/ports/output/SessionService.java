package microservice.auth_service.app.auth.core.ports.output;

import java.time.Duration;
import java.util.Optional;

import microservice.auth_service.app.auth.core.domain.models.JWTSessions;

public interface SessionService {
  void blacklistRefreshToken(String refreshToken, Duration duration);

  boolean isBlacklisted(String refreshToken);

  void blackListAllRefreshTokensForUser(String userId);

  void save(JWTSessions session);

  Optional<JWTSessions> findUserJwtSessions(String userId);
}
