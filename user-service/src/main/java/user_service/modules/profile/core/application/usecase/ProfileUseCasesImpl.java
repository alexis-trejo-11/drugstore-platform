package user_service.modules.profile.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.application.dto.CreateProfileCommand;
import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.application.dto.ProfileUpdateCommand;
import user_service.modules.profile.core.ports.input.ProfileUseCases;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Service
@RequiredArgsConstructor
public class ProfileUseCasesImpl implements ProfileUseCases {
  private final GetProfileUseCase getProfileUseCase;
  private final UpdateProfileUseCase updateProfileUseCase;

  @Override
  public void createProfile(User user, CreateProfileCommand createProfile) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public ProfileResponse getProfileByUserId(UserId userId) {
    return getProfileUseCase.execute(userId);
  }

  @Override
  public ProfileResponse updateProfile(ProfileUpdateCommand profileUpdate) {
    return updateProfileUseCase.execute(profileUpdate);
  }

  @Override
  public void deleteProfileByUserId(UserId userId) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

}
