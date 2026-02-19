package io.github.alexisTrejo11.drugstore.users.app.profile.infrastructure.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.response.ResponseWrapper;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.application.dto.ProfileResponse;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.application.dto.ProfileUpdateCommand;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.Profile;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.ports.input.ProfileUseCases;
import io.github.alexisTrejo11.drugstore.users.app.profile.infrastructure.web.rest.annotation.GetCurrentProfileOperation;
import io.github.alexisTrejo11.drugstore.users.app.profile.infrastructure.web.rest.annotation.UpdateProfileOperation;
import io.github.alexisTrejo11.drugstore.users.app.profile.infrastructure.web.rest.annotation.UpdateProfileRequestBody;
import io.github.alexisTrejo11.drugstore.users.app.profile.infrastructure.web.rest.dto.UpdateProfileRequest;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

/**
 * REST Controller for managing user profiles.
 *
 * Provides endpoints for retrieving and updating user profile information including
 * personal data (name, date of birth, gender), bio, and profile picture URL.
 *
 * All endpoints require authentication via JWT Bearer token and are rate-limited
 * to prevent abuse (100 requests per minute per user).
 *
 * Endpoints:
 * - GET  /api/v2/users/profile/me - Retrieve current user's profile
 * - PATCH /api/v2/users/profile  - Update current user's profile
 */
@RestController
@RequestMapping("/api/v2/users/profile")
@Tag(name = "Profile Management", description = "Endpoints for managing user profile information (retrieve and update personal data)")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {
  private final ProfileUseCases profileService;

  @Autowired
  public ProfileController(ProfileUseCases profileService) {
    this.profileService = profileService;
  }

  @GetMapping("/me")
  @RateLimit
  @GetCurrentProfileOperation
  public ResponseWrapper<ProfileResponse> myProfile(@RequestAttribute("userId") String userId) {
    UserId userIdObj = new UserId(userId);
    Profile profile = profileService.getProfileByUserId(userIdObj);

    var profileResponse = ProfileResponse.from(profile);
    return ResponseWrapper.success(profileResponse, "User profile retrieved successfully");
  }

  @PatchMapping
  @RateLimit
  @UpdateProfileOperation
  public ResponseWrapper<ProfileResponse> updateProfile(
      @UpdateProfileRequestBody
      @Valid @NotNull UpdateProfileRequest profileUpdate,
      @RequestAttribute("userId") String userId) {
    UserId userIdObj = new UserId(userId);

    ProfileUpdateCommand command = profileUpdate.toProfileUpdate(userIdObj);
    Profile updatedProfile = profileService.updateProfile(command);

    var profileResponse = ProfileResponse.from(updatedProfile);
    return ResponseWrapper.success(profileResponse, "User profile updated successfully");
  }
}
