package microservice.auth.app.core.ports.output;

import microservice.auth.app.core.models.Session;

import java.util.Optional;

public interface SessionRepository {
    void save(Session session);
    Optional<Session> findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
}
