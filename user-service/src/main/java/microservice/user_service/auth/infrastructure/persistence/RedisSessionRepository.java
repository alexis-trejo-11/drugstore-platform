package microservice.user_service.auth.infrastructure.persistence;

import microservice.user_service.auth.core.domain.session.Session;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface RedisSessionRepository extends CrudRepository<Session, String> {

}
