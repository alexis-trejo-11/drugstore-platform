package io.github.alexisTrejo11.drugstore.users.profile.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.users.profile.core.domain.exception.UserProfileNotFoundError;
import io.github.alexisTrejo11.drugstore.users.profile.core.ports.output.ProfileRepository;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

@Component
public class DeleteProfileUseCase {
  private final ProfileRepository profileRepository;

  @Autowired
  public DeleteProfileUseCase(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  public void execute(UserId userId) {
    if (profileRepository.findByUserId(userId).isEmpty()) {
      throw new UserProfileNotFoundError(userId);
    }

    profileRepository.deleteByUserId(userId);
  }

}
