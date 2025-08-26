package user_service.modules.profile.core.ports.input;

import java.util.UUID;

import user_service.modules.auth.core.application.dto.CreateProfileDTO;
import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.users.core.application.dto.ProfileUpdate;
import user_service.modules.users.core.domain.models.entities.User;

public interface ProfileFacadeService {
    ProfileResponse getProfileByUserId(UUID userId);

    void createProfile(User user, CreateProfileDTO createProfile);

    ProfileResponse updateProfile(ProfileUpdate profileUpdate);

    void deleteProfileByUserId(UUID userId);
}
