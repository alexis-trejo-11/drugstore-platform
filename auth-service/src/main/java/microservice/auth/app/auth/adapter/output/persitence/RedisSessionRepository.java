package microservice.auth.app.auth.adapter.output.persitence;

import lombok.RequiredArgsConstructor;
import microservice.auth.app.auth.core.domain.models.JWTSessions;
import microservice.auth.app.auth.core.ports.output.SessionService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisSessionRepository implements SessionService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SESSION_PREFIX = "session:";

	@Override
		public void blacklistRefreshToken(String refreshToken, Duration duration) {
				String key = "blacklist:" + refreshToken;
				redisTemplate.opsForValue().set(key, true, duration);
		}

	@Override
		public boolean isBlacklisted(String refreshToken) {
				String key = "blacklist:" + refreshToken;
				Boolean isBlacklisted = (Boolean) redisTemplate.opsForValue().get(key);
				return isBlacklisted != null && isBlacklisted;
		}

    @Override
    public void save(JWTSessions session) {
        String key = SESSION_PREFIX + session.getSessionId();
        redisTemplate.opsForValue().set(key, session,
                Duration.between(LocalDateTime.now(), session.getExpiresAt()));
    }

    @Override
    public Optional<JWTSessions> findBySessionId(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
	    JWTSessions session = (JWTSessions) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(session);
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }
}
