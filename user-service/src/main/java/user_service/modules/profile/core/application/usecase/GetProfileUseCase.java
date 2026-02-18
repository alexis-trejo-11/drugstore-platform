package user_service.modules.profile.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.domain.exception.UserProfileNotFoundError;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Service
public class GetProfileUseCase {
  private final ProfileRepository profileRepository;

  @Autowired
  public GetProfileUseCase(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  public ProfileResponse execute(UserId userId) {
    Profile profile = profileRepository.findByUserId(userId)
        .orElseThrow(() -> new UserProfileNotFoundError(userId));

    return ProfileResponse.from(profile);
  }
}