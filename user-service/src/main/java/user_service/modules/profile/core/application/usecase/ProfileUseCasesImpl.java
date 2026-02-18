package user_service.modules.profile.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.application.dto.CreateProfileCommand;
import user_service.modules.profile.core.application.dto.ProfileUpdateCommand;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.input.ProfileUseCases;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Service
@RequiredArgsConstructor
public class ProfileUseCasesImpl implements ProfileUseCases {
  private final GetProfileUseCase getProfileUseCase;
  private final CreateUserProfileUseCase createProfileUseCase;
  private final UpdateProfileUseCase updateProfileUseCase;
  private final DeleteProfileUseCase deleteProfileUseCase;

  @Override
  public void createProfile(User user, CreateProfileCommand createProfile) {
    createProfileUseCase.execute(user, createProfile);
  }

  @Override
  public Profile getProfileByUserId(UserId userId) {
    return getProfileUseCase.execute(userId);
  }

  @Override
  public Profile updateProfile(ProfileUpdateCommand profileUpdate) {
    return updateProfileUseCase.execute(profileUpdate);
  }

  @Override
  public void deleteProfileByUserId(UserId userId) {
    deleteProfileUseCase.execute(userId);
  }

}
