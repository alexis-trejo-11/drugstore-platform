package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.twofa;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.twoFa.VerifyTwoFactorCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions.TwoFactorAuthenticationException;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VerifyTwoFactorCodeUseCase - Verifies 2FA code entered by user.
 * This DDD ApplicationService validates the code against stored token.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyTwoFactorCodeUseCase {
  private final TokenService tokenService;

  /**
   * Execute the verify 2FA code use case.
   *
   * @param command the verify 2FA code command containing user ID and code
   * @throws TwoFactorAuthenticationException if code is invalid or expired
   */
  public void execute(VerifyTwoFactorCommand command) {
    log.info("Processing 2FA code verification for user: {}", command.userId());

    validateCode(command.code());

    log.info("2FA code verified successfully for user: {}", command.userId());
  }

  /**
   * Validates the 2FA code.
   */
  private void validateCode(String code) {
    log.debug("Validating 2FA code");

    if (code == null || code.isBlank()) {
      log.warn("Empty 2FA code provided");
      throw TwoFactorAuthenticationException.invalidCode();
    }

    if (!tokenService.validateToken(code, TokenType.TWO_FA)) {
      log.warn("Invalid or expired 2FA code");
      throw TwoFactorAuthenticationException.invalidCode();
    }

    log.debug("2FA code is valid");
  }
}
