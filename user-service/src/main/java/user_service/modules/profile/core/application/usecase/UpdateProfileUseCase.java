package user_service.modules.profile.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.users.core.application.dto.ProfileUpdate;
import user_service.utils.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCase {
    private final ProfileRepository profileRepository;

    public ProfileResponse execute(ProfileUpdate update) {
        Profile profile = profileRepository.findByUserId(update.userId())
                .orElseThrow(() -> new NotFoundException("Profile", update.userId().toString()));

        profile.updateProfileInfo(profile.getBio(), update.avatarUrl(), update.coverUrl());
        profile.updatePersonalInfo(update.firstName(), update.lastName(), update.dateOfBirth(), update.gender());
        Profile updatedProfile = profileRepository.save(profile);

        return ProfileResponse.from(updatedProfile);
    }
}
