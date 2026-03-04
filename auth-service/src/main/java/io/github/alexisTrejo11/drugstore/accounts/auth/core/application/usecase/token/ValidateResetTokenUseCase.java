package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.token;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.ValidateResetTokenCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.InvalidTokenException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.TokenExpiredException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ValidateResetTokenUseCase - Validates password reset tokens.
 * This DDD ApplicationService checks if a reset token is valid and not expired.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ValidateResetTokenUseCase {
  private final TokenService tokenService;

  /**
   * Execute the validate reset token use case.
   *
   * @param command the validate reset token command
   * @throws InvalidTokenException if token is invalid or malformed
   * @throws TokenExpiredException if token has expired
   */
  public void execute(ValidateResetTokenCommand command) {
    log.info("Validating password reset token");

    validateToken(command.token());

    log.info("Password reset token is valid");
  }

  /**
   * Validates the reset token.
   */
  private void validateToken(String resetToken) {
    log.debug("Checking reset token validity");

    if (!tokenService.validateToken(resetToken, TokenType.ACTIVATION)) {
      log.warn("Invalid or expired password reset token");
      throw TokenExpiredException.temporary("Password reset");
    }

    log.debug("Reset token is valid");
  }
}
