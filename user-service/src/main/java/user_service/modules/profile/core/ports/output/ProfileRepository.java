package user_service.modules.profile.core.ports.output;

import java.util.Optional;
import java.util.UUID;

import user_service.modules.profile.core.domain.model.Profile;

public interface ProfileRepository {
    Profile save(Profile profile);

    Optional<Profile> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
