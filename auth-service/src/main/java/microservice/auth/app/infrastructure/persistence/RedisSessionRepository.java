package microservice.auth.app.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import microservice.auth.app.core.models.Session;
import microservice.auth.app.core.ports.output.SessionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisSessionRepository implements SessionRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SESSION_PREFIX = "session:";

    @Override
    public void save(Session session) {
        String key = SESSION_PREFIX + session.getSessionId();
        redisTemplate.opsForValue().set(key, session,
                Duration.between(LocalDateTime.now(), session.getExpiresAt()));
    }

    @Override
    public Optional<Session> findBySessionId(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        Session session = (Session) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(session);
    }

    @Override
    public void deleteBySessionId(String sessionId) {
        String key = SESSION_PREFIX + sessionId;
        redisTemplate.delete(key);
    }
}
