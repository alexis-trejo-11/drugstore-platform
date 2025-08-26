package user_service.modules.users.infrastructure.adapter.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import user_service.modules.users.infrastructure.adapter.persistence.models.ProfileModel;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileModel, UUID> {
    Optional<ProfileModel> findByUserId(UUID userId);
}
