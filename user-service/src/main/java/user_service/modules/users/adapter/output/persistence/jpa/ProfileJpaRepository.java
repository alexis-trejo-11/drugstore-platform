package user_service.modules.users.adapter.output.persistence.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import user_service.modules.users.adapter.output.persistence.models.ProfileModel;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileModel, String> {
  Optional<ProfileModel> findByUserId(String userId);
}
