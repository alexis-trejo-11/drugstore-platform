package user_service.modules.profile.core.domain.model.entities;

import lombok.Builder;
import user_service.modules.profile.core.domain.model.valueobjects.PersonalData;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Builder
public record CreateProfileParams(
    UserId userId,
    PersonalData personalData,
    String bio,
    String avatarUrl,
    String coverPic) {

  // Convenience constructor with defaults
  public CreateProfileParams(UserId userId, PersonalData personalData) {
    this(userId, personalData, "", "default-avatar.png", "default-cover.png");
  }
}