package io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.entities;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.valueobjects.PersonalData;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

@Builder
public record CreateProfileParams(
    UserId userId,
    PersonalData personalData,
    String bio,
    String profilePictureUrl) {

  // Convenience constructor with defaults
  public CreateProfileParams(UserId userId, PersonalData personalData) {
    this(userId, personalData, "", "");
  }
}