package io.github.alexisTrejo11.drugstore.users.profile.core.ports.output;

import java.util.Optional;

import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

public interface ProfileRepository {
  Profile save(Profile profile);

  Optional<Profile> findByUserId(UserId userId);

  void deleteByUserId(UserId userId);
}
