package io.github.alexisTrejo11.drugstore.users.profile.core.ports.input;

import io.github.alexisTrejo11.drugstore.users.profile.core.application.dto.CreateProfileCommand;
import io.github.alexisTrejo11.drugstore.users.profile.core.application.dto.ProfileUpdateCommand;
import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

public interface ProfileUseCases {
  Profile getProfileByUserId(UserId userId);

  void createProfile(User user, CreateProfileCommand createProfile);

  Profile updateProfile(ProfileUpdateCommand profileUpdate);

  void deleteProfileByUserId(UserId userId);
}
