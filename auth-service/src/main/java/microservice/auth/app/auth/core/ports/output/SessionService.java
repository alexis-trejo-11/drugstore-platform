package microservice.auth.app.auth.core.ports.output;

import java.time.Duration;
import java.util.Optional;

import microservice.auth.app.auth.core.domain.models.JWTSessions;

public interface SessionService {
  void blacklistRefreshToken(String refreshToken, Duration duration);

  boolean isBlacklisted(String refreshToken);

  void invalidateAllSessionsForUser(String userId);

  void save(JWTSessions session);
   Optional<JWTSessions> findUserJwtSessions(String userId);
  // void deleteBySessionId(String sessionId);
}
