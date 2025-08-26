package user_service.modules.auth.infrastructure.adapters.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import user_service.modules.auth.core.domain.repositories.SessionRepository;
import user_service.modules.auth.core.domain.session.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisSessionRepository implements SessionRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_KEY_PREFIX = "session:";
    private static final String USER_SESSIONS_KEY_PREFIX = "user_sessions:";
    private static final String ACTIVE_SESSIONS_KEY = "active_sessions";

    @Override
    public Session save(Session session) {
        try {
            String sessionKey = getSessionKey(session.getRefreshToken());
            String userSessionsKey = getUserSessionsKey(session.getUserId());

            // Guardar la sesión principal
            redisTemplate.opsForValue().set(
                    sessionKey,
                    session,
                    Duration.ofMillis(session.getExpiresInMillis()));

            // Guardar solo el token en el set de usuario (no el objeto completo)
            redisTemplate.opsForSet().add(userSessionsKey, session.getRefreshToken());

            // Guardar solo el token en sesiones activas
            redisTemplate.opsForSet().add(ACTIVE_SESSIONS_KEY, session.getRefreshToken());

            // Establecer expiración para la lista de sesiones del usuario
            redisTemplate.expire(userSessionsKey, 30, TimeUnit.DAYS);

            log.debug("Session saved for user: {}", session.getUserId());
            return session;

        } catch (Exception e) {
            log.error("Error saving session for user: {}", session.getUserId(), e);
            throw new RuntimeException("Failed to save session", e);
        }
    }

    @Override
    public Optional<Session> findUserSession(String token, UUID userId) {
        try {
            String sessionKey = getSessionKey(token);
            Object sessionObj = redisTemplate.opsForValue().get(sessionKey);

            if (sessionObj instanceof Session) {
                Session session = (Session) sessionObj;
                if (session.getUserId().toString().equals(userId.toString())) {
                    return Optional.of(session);
                }
            }
            return Optional.empty();

        } catch (Exception e) {
            log.error("Error finding session for token: {}", token, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Session> findAllByUser(UUID userId) {
        try {
            String userSessionsKey = getUserSessionsKey(userId);
            Set<Object> sessionTokens = redisTemplate.opsForSet().members(userSessionsKey);

            if (sessionTokens == null || sessionTokens.isEmpty()) {
                return List.of();
            }

            return sessionTokens.stream()
                    .map(token -> {
                        if (token instanceof String) {
                            String sessionKey = getSessionKey((String) token);
                            Object sessionObj = redisTemplate.opsForValue().get(sessionKey);
                            if (sessionObj instanceof Session) {
                                return (Session) sessionObj;
                            }
                        }
                        return null;
                    })
                    .filter(session -> session != null && !session.isExpired())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error finding sessions for user: {}", userId, e);
            return List.of();
        }
    }

    @Override
    public void deactivateAllUserSessions(UUID userId) {
        try {
            String userSessionsKey = getUserSessionsKey(userId);
            Set<Object> sessionTokens = redisTemplate.opsForSet().members(userSessionsKey);

            if (sessionTokens != null) {
                sessionTokens.forEach(token -> {
                    if (token instanceof String) {
                        String sessionKey = getSessionKey((String) token);
                        redisTemplate.delete(sessionKey);
                        redisTemplate.opsForSet().remove(ACTIVE_SESSIONS_KEY, token);
                    }
                });
            }

            // Eliminar la lista de sesiones del usuario
            redisTemplate.delete(userSessionsKey);

            log.info("All sessions deactivated for user: {}", userId);

        } catch (Exception e) {
            log.error("Error deactivating all sessions for user: {}", userId, e);
            throw new RuntimeException("Failed to deactivate sessions", e);
        }
    }

    @Override
    public void deactivateUserSession(UUID userId, String token) {
        try {
            String sessionKey = getSessionKey(token);
            String userSessionsKey = getUserSessionsKey(userId);

            // Eliminar la sesión principal
            redisTemplate.delete(sessionKey);

            // Remover de la lista de sesiones del usuario
            redisTemplate.opsForSet().remove(userSessionsKey, token);

            // Remover de sesiones activas
            redisTemplate.opsForSet().remove(ACTIVE_SESSIONS_KEY, token);

            log.debug("Session deactivated for user: {}", userId);

        } catch (Exception e) {
            log.error("Error deactivating session for user: {}", userId, e);
            throw new RuntimeException("Failed to deactivate session", e);
        }
    }

    // Métodos auxiliares
    private String getSessionKey(String token) {
        return SESSION_KEY_PREFIX + token;
    }

    private String getUserSessionsKey(UUID userId) {
        return USER_SESSIONS_KEY_PREFIX + userId.toString();
    }

    // Métodos adicionales útiles
    public long countActiveSessions() {
        Long size = redisTemplate.opsForSet().size(ACTIVE_SESSIONS_KEY);
        return size != null ? size : 0L;
    }

    public void cleanupExpiredSessions() {
        try {
            Set<Object> activeTokens = redisTemplate.opsForSet().members(ACTIVE_SESSIONS_KEY);
            if (activeTokens != null) {
                activeTokens.forEach(token -> {
                    if (token instanceof String) {
                        String sessionKey = getSessionKey((String) token);
                        Boolean exists = redisTemplate.hasKey(sessionKey);
                        if (Boolean.FALSE.equals(exists)) {
                            redisTemplate.opsForSet().remove(ACTIVE_SESSIONS_KEY, token);
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error cleaning up expired sessions", e);
        }
    }

    public Session findByToken(String token) {
        try {
            String sessionKey = getSessionKey(token);
            Object sessionObj = redisTemplate.opsForValue().get(sessionKey);
            return (sessionObj instanceof Session) ? (Session) sessionObj : null;
        } catch (Exception e) {
            log.error("Error finding session by token: {}", token, e);
            return null;
        }
    }
}