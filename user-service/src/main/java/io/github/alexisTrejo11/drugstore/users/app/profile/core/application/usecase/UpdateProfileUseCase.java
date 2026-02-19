package io.github.alexisTrejo11.drugstore.users.app.profile.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.users.app.profile.core.application.dto.ProfileUpdateCommand;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.exception.UserProfileNotFoundError;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.ports.output.ProfileRepository;

@Service
public class UpdateProfileUseCase {
  private final ProfileRepository profileRepository;

  @Autowired
  public UpdateProfileUseCase(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  public Profile execute(ProfileUpdateCommand command) {
    Profile profile = profileRepository.findByUserId(command.userId())
        .orElseThrow(() -> new UserProfileNotFoundError(command.userId()));

    profile.updateProfileInfo(profile.getBio(), command.profilePictureUrl());
    profile.updatePersonalInfo(command.fullName(), command.dateOfBirth(), command.gender());

    return profileRepository.save(profile);
  }
}
