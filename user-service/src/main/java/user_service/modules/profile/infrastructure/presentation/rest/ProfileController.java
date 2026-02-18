package user_service.modules.profile.infrastructure.presentation.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import user_service.modules.profile.core.application.dto.ProfileResponse;
import user_service.modules.profile.core.application.dto.ProfileUpdateCommand;
import user_service.modules.profile.core.ports.input.ProfileUseCases;
import user_service.modules.profile.infrastructure.presentation.rest.dto.UpdateProfileRequest;
import user_service.modules.users.core.domain.models.entities.User;
import user_service.utils.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v2/users/profile")
@RequiredArgsConstructor
public class ProfileController {
  private final ProfileUseCases profileService;

  @GetMapping
  public ResponseWrapper<ProfileResponse> getProfile(@AuthenticationPrincipal User user) {
    ProfileResponse response = profileService.getProfileByUserId(user.getId());
    return ResponseWrapper.success(response, "User profile retrieved successfully");
  }

  @PatchMapping
  public ResponseWrapper<?> updateProfile(
      @Valid UpdateProfileRequest profileUpdate,
      @AuthenticationPrincipal User user) {
    ProfileUpdateCommand command = profileUpdate.toProfileUpdate(user.getId());
    profileService.updateProfile(command);
    return ResponseWrapper.success(null, "User profile updated successfully");
  }
}
