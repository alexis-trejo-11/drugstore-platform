package io.github.alexisTrejo11.drugstore.users.app.profile.core.application.usecase;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.application.dto.CreateProfileCommand;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.application.dto.ProfileUpdateCommand;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.ports.input.ProfileUseCases;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

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
