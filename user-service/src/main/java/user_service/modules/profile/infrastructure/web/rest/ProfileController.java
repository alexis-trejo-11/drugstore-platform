package user_service.modules.profile.infrastructure.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.response.ResponseWrapper;
import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.application.dto.ProfileUpdateCommand;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.profile.core.ports.input.ProfileUseCases;
import user_service.modules.profile.infrastructure.web.rest.dto.UpdateProfileRequest;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@RestController
@RequestMapping("/api/v2/users/profile")
public class ProfileController {
  private final ProfileUseCases profileService;

  @Autowired
  public ProfileController(ProfileUseCases profileService) {
    this.profileService = profileService;
  }

  @GetMapping("/me")
  @RateLimit
  public ResponseWrapper<ProfileResponse> myProfile(@RequestAttribute("userId") String userId) {
    UserId userIdObj = new UserId(userId);
    Profile profile = profileService.getProfileByUserId(userIdObj);

    var profileResponse = ProfileResponse.from(profile);
    return ResponseWrapper.success(profileResponse, "User profile retrieved successfully");
  }

  @PatchMapping
  @RateLimit
  public ResponseWrapper<ProfileResponse> updateProfile(
      @Valid @NotNull UpdateProfileRequest profileUpdate,
      @RequestAttribute("userId") String userId) {
    UserId userIdObj = new UserId(userId);

    ProfileUpdateCommand command = profileUpdate.toProfileUpdate(userIdObj);
    Profile updatedProfile = profileService.updateProfile(command);

    var profileResponse = ProfileResponse.from(updatedProfile);
    return ResponseWrapper.success(profileResponse, "User profile updated successfully");
  }
}
