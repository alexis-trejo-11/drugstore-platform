package user_service.modules.profile.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import user_service.modules.profile.core.domain.exception.UserProfileNotFoundError;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

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
