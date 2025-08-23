package microservice.user_service.users.infrastructure.output.persistence.repositories;

import microservice.user_service.users.core.domain.models.enums.UserRole;
import microservice.user_service.users.core.domain.models.enums.UserStatus;
import microservice.user_service.users.infrastructure.output.persistence.models.UserModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findByPhoneNumber(String phoneNumber);

    Page<UserModel> findByRole(UserRole role, Pageable pageable);

    Page<UserModel> findByStatus(UserStatus status, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE UserModel u SET u.lastLoginAt = :timestamp, u.updatedAt = :timestamp WHERE u.id = :id")
    void updateLastLogin(UUID id);
}
