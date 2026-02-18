package user_service.modules.profile.core.ports.input;

import user_service.modules.profile.core.application.dto.CreateProfileCommand;

import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.application.dto.ProfileUpdateCommand;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public interface ProfileUseCases {
  ProfileResponse getProfileByUserId(UserId userId);

  void createProfile(User user, CreateProfileCommand createProfile);

  ProfileResponse updateProfile(ProfileUpdateCommand profileUpdate);

  void deleteProfileByUserId(UserId userId);
}
