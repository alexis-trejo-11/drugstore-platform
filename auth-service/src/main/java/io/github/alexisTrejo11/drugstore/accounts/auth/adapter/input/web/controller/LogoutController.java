package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.LogoutRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutAllCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.LogoutUseCases;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.LogoutAllOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.LogoutOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v2/auth")
@Tag(
    name = "Logout",
    description =
        "Revoke refresh sessions. **Single-device logout** is public; **logout all** requires JWT.")
public class LogoutController {
  private final LogoutUseCases logoutUseCases;

  @Autowired
  public LogoutController(LogoutUseCases logoutUseCases) {
    this.logoutUseCases = logoutUseCases;
  }

  @PostMapping("/logout")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @LogoutOperation
  public ResponseEntity<ResponseWrapper<Void>> logout(
      @RequestBody @Valid @NotNull LogoutRequest request) {
    log.info("Logout request received");

    LogoutCommand command = new LogoutCommand(request.refreshToken());
    logoutUseCases.logout(command);

    log.info("Logout completed successfully");
    return ResponseEntity.ok(ResponseWrapper.success(null, "Logout successfully processed"));
  }

  @PostMapping("/logout-all")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @LogoutAllOperation
  public ResponseEntity<ResponseWrapper<Void>> logoutAll(
      @RequestAttribute("userId") String userId) {
    log.info("Logout all sessions request received for user: {}", userId);

    LogoutAllCommand command = new LogoutAllCommand(userId);
    logoutUseCases.logoutAll(command);

    log.info("All sessions logged out successfully for user: {}", userId);
    return ResponseEntity.ok(ResponseWrapper.success(null, "All sessions logged out successfully"));
  }
}
