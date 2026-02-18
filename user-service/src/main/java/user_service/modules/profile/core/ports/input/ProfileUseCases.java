package user_service.modules.profile.core.ports.input;

import user_service.modules.profile.core.application.dto.CreateProfileCommand;
import user_service.modules.profile.core.application.dto.ProfileUpdateCommand;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public interface ProfileUseCases {
  Profile getProfileByUserId(UserId userId);

  void createProfile(User user, CreateProfileCommand createProfile);

  Profile updateProfile(ProfileUpdateCommand profileUpdate);

  void deleteProfileByUserId(UserId userId);
}
