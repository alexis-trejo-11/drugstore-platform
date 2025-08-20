package microservice.user_service.users.infrastructure.persistence.repositories;

import microservice.user_service.users.infrastructure.persistence.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);
}
