package user_service.modules.profile.core.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.CreateProfileDTO;
import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.ports.input.ProfileFacadeService;
import user_service.modules.users.core.application.dto.ProfileUpdate;
import user_service.modules.users.core.domain.models.entities.User;

@Service
@RequiredArgsConstructor
public class ProfileUseCases implements ProfileFacadeService {
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    @Override
    public void createProfile(User user, CreateProfileDTO createProfile) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ProfileResponse getProfileByUserId(UUID userId) {
        return getProfileUseCase.execute(userId);
    }

    @Override
    public ProfileResponse updateProfile(ProfileUpdate profileUpdate) {
        return updateProfileUseCase.execute(profileUpdate);
    }

    @Override
    public void deleteProfileByUserId(UUID userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
