package microservice.user_service.users.core.ports.output;

import java.util.UUID;

import microservice.user_service.users.core.domain.models.entities.Profile;

public interface ProfileRepository {
    Profile save(Profile profile);

    Profile findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
