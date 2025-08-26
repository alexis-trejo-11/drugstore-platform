package user_service.modules.profile.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.auth.core.application.dto.CreateProfileDTO;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.utils.exceptions.ConflictException;

@Service
@RequiredArgsConstructor
public class CreateUserProfileUseCase {
    private final ProfileRepository profileRepository;

    public void execute(User user, CreateProfileDTO createProfile) {
        profileRepository.findByUserId(user.getId()).orElseThrow(
                () -> new ConflictException("Profile already exists for user with id: " + user.getId().toString(),
                        "PROFILE_ALREADY_EXISTS"));

        Profile profile = Profile.CreateUserProfile(
                user.getId(),
                createProfile.getFirstName(),
                createProfile.getLastName(),
                createProfile.getBirthDate(),
                createProfile.getGender());

        profileRepository.save(profile);
    }
}
