package microservice.user_service.auth.core.ports.output;


import microservice.user_service.auth.core.domain.session.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    void create(Session session);
    List<Session> findByUserId(String userId);
    Optional<Session> findByIdAndUserId(String id, String userId);
    void deleteByIdAndUserId(String sessionId, String userId);
    void deleteAllByUserId(String userId);
}
