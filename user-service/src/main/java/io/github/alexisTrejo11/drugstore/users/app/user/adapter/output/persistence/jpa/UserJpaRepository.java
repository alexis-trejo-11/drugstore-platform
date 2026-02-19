package io.github.alexisTrejo11.drugstore.users.app.user.adapter.output.persistence.jpa;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import io.github.alexisTrejo11.drugstore.users.app.user.adapter.output.persistence.models.UserModel;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserStatus;

@Repository
public interface UserJpaRepository extends JpaRepository<UserModel, String> {
    Optional<UserModel> findByEmail(String email);

    Optional<UserModel> findByPhoneNumber(String phoneNumber);

    Page<UserModel> findByRole(UserRole role, Pageable pageable);

    Page<UserModel> findByStatus(UserStatus status, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE UserModel u SET u.lastLoginAt = CURRENT_TIMESTAMP, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void updateLastLogin(@Param("id") String id);

}
