package io.github.alexisTrejo11.drugstore.users.app.profile.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.exception.UserProfileNotFoundError;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.ports.output.ProfileRepository;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

@Service
public class GetProfileUseCase {
  private final ProfileRepository profileRepository;

  @Autowired
  public GetProfileUseCase(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  public Profile execute(UserId userId) {
    return profileRepository.findByUserId(userId)
        .orElseThrow(() -> new UserProfileNotFoundError(userId));
  }
}