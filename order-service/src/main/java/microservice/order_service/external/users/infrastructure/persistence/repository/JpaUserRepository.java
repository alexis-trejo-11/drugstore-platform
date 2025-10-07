package microservice.order_service.external.users.infrastructure.persistence.repository;

import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserModel, String> {

    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByPhoneNumber(String phoneNumber);

}
