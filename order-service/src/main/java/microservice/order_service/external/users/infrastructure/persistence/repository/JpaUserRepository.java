package microservice.order_service.external.users.infrastructure.persistence.repository;

import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserModel, String> {
}
