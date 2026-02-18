package user_service.modules.profile.infrastructure.presentation.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import user_service.modules.profile.core.application.dto.ProfileUpdateCommand;
import user_service.modules.users.core.domain.models.enums.Gender;
import user_service.modules.users.core.domain.models.valueobjects.FullName;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

public record UpdateProfileRequest(
    @NotEmpty(message = "First name cannot be empty") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,
    @NotEmpty(message = "Last name cannot be empty") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,
    Gender gender,
    String bio,
    String avatarUrl,
    String coverUrl,

    @Past(message = "Date of birth must be in the past") LocalDateTime dateOfBirth) {

  public ProfileUpdateCommand toProfileUpdate(UserId userId) {
    FullName fullName = (firstName != null && lastName != null) ? new FullName(firstName, lastName) : null;

    return ProfileUpdateCommand.builder()
        .userId(userId)
        .avatarUrl(avatarUrl != null ? avatarUrl : null)
        .bio(bio != null ? bio : null)
        .coverUrl(coverUrl != null ? coverUrl : null)
        .dateOfBirth(dateOfBirth != null ? dateOfBirth.toLocalDate() : null)
        .fullName(fullName)
        .gender(gender != null ? gender : null)
        .build();
  }
}
