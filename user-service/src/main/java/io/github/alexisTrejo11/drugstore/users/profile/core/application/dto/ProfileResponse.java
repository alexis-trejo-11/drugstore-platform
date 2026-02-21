package io.github.alexisTrejo11.drugstore.users.profile.core.application.dto;

import java.time.format.DateTimeFormatter;

import io.github.alexisTrejo11.drugstore.users.profile.core.domain.model.Profile;

/**
 * Response DTO for profile data.
 *
 * Represents the response payload when retrieving or updating user profile information.
 * All fields are in serializable format suitable for HTTP responses.
 *
 * Date Format:
 * - dateOfBirth uses BASIC_ISO_DATE pattern: YYYYMMDD (e.g., "19900515")
 * - Null if user has not provided a date of birth
 *
 * @see Profile for domain model
 */
public record ProfileResponse(
    String firstName,
    String lastName,
    String dateOfBirth,
    String gender,
    String bio,
    String profilePictureUrl
) {

  /**
   * Converts domain Profile object to response DTO.
   *
   * Transformations:
   * - Extracts firstName and lastName from FullName value object
   * - Formats LocalDate to BASIC_ISO_DATE (YYYYMMDD)
   * - Converts Gender enum to string
   * - Handles null values gracefully
   *
   * @param profile Domain model containing user profile information
   * @return ProfileResponse with all fields populated
   */
  public static ProfileResponse from(Profile profile) {
    return new ProfileResponse(
        profile.getFirstName(),
        profile.getLastName(),
        profile.getDateOfBirth() != null
            ? profile.getDateOfBirth().format(DateTimeFormatter.BASIC_ISO_DATE)
            : null,
        profile.getGender() != null ? profile.getGender().name() : null,
        profile.getBio(),
        profile.getProfilePictureUrl());
  }
}
