package user_service.modules.profile.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.application.dto.ProfileUpdateCommand;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.utils.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
public class UpdateProfileUseCase {
  private final ProfileRepository profileRepository;

  public ProfileResponse execute(ProfileUpdateCommand command) {
    Profile profile = profileRepository.findByUserId(command.userId())
        .orElseThrow(() -> new NotFoundException("Profile", command.userId().toString()));

    profile.updateProfileInfo(profile.getBio(), command.avatarUrl(), command.coverUrl());
    profile.updatePersonalInfo(command.fullName(), command.dateOfBirth(), command.gender());

    Profile updatedProfile = profileRepository.save(profile);
    return ProfileResponse.from(updatedProfile);
  }
}
