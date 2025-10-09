package microservice.order_service.external.users.infrastructure.persistence.repository;

import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserModel, String> {
    @Query("SELECT u FROM UserModel u JOIN FETCH u.addresses WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<UserModel> findActiveByIdWithAddress(@Param(value = "id") String id);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<UserModel> findByEmailAndDeletedAtIsNull(String email);

    Optional<UserModel> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber);
}
