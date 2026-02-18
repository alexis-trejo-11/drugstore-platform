package user_service.modules.profile.core.ports.output;

import java.util.Optional;

import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public interface ProfileRepository {
  Profile save(Profile profile);

  Optional<Profile> findByUserId(UserId userId);

  void deleteByUserId(UserId userId);
}
