package user_service.modules.auth.core.domain.repositories;

import user_service.modules.auth.core.domain.session.Session;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository {
    Session save(Session session);

    Optional<Session> findUserSession(String token, UUID userId);

    List<Session> findAllByUser(UUID userId);

    void deactivateAllUserSessions(UUID userId);

    void deactivateUserSession(UUID userId, String token);
}
