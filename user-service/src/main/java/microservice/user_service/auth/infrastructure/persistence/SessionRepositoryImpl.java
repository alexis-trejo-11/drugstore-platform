package microservice.user_service.auth.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import microservice.user_service.auth.core.domain.repositories.SessionRepository;
import microservice.user_service.auth.core.domain.session.Session;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    @Override
    public Session save(Session session) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public Session findUserSession(String token, String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findUserSession'");
    }

    @Override
    public List<Session> findAllByUser(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllByUser'");
    }

    @Override
    public void deactivateAllUserSessions(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deactivateAllUserSessions'");
    }

    @Override
    public void deactivateUserSession(String userId, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deactivateUserSession'");
    }

}
