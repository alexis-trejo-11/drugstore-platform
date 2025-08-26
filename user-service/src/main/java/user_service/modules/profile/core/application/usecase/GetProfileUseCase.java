package user_service.modules.profile.core.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.domain.exception.UserProfileNotFoundError;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.output.ProfileRepository;

@Service
@RequiredArgsConstructor
public class GetProfileUseCase {
    private final ProfileRepository profileRepository;

    public ProfileResponse execute(UUID userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new UserProfileNotFoundError(userId));

        return ProfileResponse.from(profile);
    }
}