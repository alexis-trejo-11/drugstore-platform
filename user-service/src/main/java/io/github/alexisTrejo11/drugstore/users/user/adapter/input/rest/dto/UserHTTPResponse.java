package io.github.alexisTrejo11.drugstore.users.user.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;

/**
 * Data Transfer Object for user information responses.
 * Contains user details returned by query endpoints.
 */
@Builder
@Schema(description = "User information response containing user profile details")
public record UserHTTPResponse(
    @Schema(description = "Unique user identifier (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", format = "uuid") String id,

    @Schema(description = "User's email address", example = "john.doe@example.com", format = "email") String email,

    @Schema(description = "User's phone number with country code", example = "+1-555-123-4567", nullable = true) String phoneNumber,

    @Schema(description = "User's role in the system", example = "CUSTOMER", allowableValues = {
        "CUSTOMER", "EMPLOYEE", "ADMIN" }) UserRole role,

    @Schema(description = "Timestamp when the user account was created", example = "2026-01-15T10:30:00", format = "date-time") String joinedAt,

    @Schema(description = "Timestamp of the user's last login", example = "2026-02-19T09:15:00", format = "date-time", nullable = true) String lastLoginAt) {

  public static UserHTTPResponse from(UserQueryResult result) {
    return new UserHTTPResponse(
        result.id() != null ? result.id().toString() : null,
        result.email() != null ? result.email().value() : null,
        result.phoneNumber() != null ? result.phoneNumber().value() : null,
        result.role(),
        result.createdAt() != null ? result.createdAt().toString() : null,
        result.lastLoginAt() != null ? result.lastLoginAt().toString() : null);
  }
}
