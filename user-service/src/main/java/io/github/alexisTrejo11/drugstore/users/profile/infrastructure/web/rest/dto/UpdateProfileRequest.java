package io.github.alexisTrejo11.drugstore.users.profile.infrastructure.web.rest.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import io.github.alexisTrejo11.drugstore.users.profile.core.application.dto.ProfileUpdateCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.Gender;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;

/**
 * Request DTO for profile update operations.
 *
 * All fields are optional - only provided fields will be updated.
 * Supports partial updates.
 *
 * @see UpdateProfileRequestBody decorator for OpenAPI documentation
 */
public record UpdateProfileRequest(
    @NotEmpty(message = "First name cannot be empty")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName,

    @NotEmpty(message = "Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

    Gender gender,

    @Size(min = 3, max = 255, message = "Bio must be between 3 and 255 characters")
    String bio,

    String profilePictureUrl,

    @Past(message = "Date of birth must be in the past")
    LocalDateTime dateOfBirth) {

  /**
   * Converts request DTO to domain command object.
   *
   * @param userId User ID from JWT token
   * @return ProfileUpdateCommand ready for domain processing
   */
  public ProfileUpdateCommand toProfileUpdate(UserId userId) {
    FullName fullName = (firstName != null && lastName != null)
        ? new FullName(firstName, lastName)
        : null;

    return ProfileUpdateCommand.builder()
        .userId(userId)
        .profilePictureUrl(profilePictureUrl != null ? profilePictureUrl : null)
        .bio(bio != null ? bio : null)
        .dateOfBirth(dateOfBirth != null ? dateOfBirth.toLocalDate() : null)
        .fullName(fullName)
        .gender(gender != null ? gender : null)
        .build();
  }
}
