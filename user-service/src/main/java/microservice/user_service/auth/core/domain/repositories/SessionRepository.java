package microservice.user_service.auth.core.domain.repositories;

import microservice.user_service.auth.core.domain.session.Session;
import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Session save(Session session);
    Session findUserSession(String token, String userId);
    List<Session> findAllByUser(String userId);
    void deactivateAllUserSessions(String userId);
    void deactivateUserSession(String userId, String token);
}
