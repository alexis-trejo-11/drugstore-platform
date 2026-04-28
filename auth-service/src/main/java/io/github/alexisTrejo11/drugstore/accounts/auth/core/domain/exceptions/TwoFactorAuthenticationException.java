package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.exceptions;

/**
 * Exception thrown when two-factor authentication verification fails.
 */
public class TwoFactorAuthenticationException extends AuthenticationException {

  public TwoFactorAuthenticationException(String message) {
    super(message);
  }

  /**
   * Creates exception for invalid 2FA code
   */
  public static TwoFactorAuthenticationException invalidCode() {
    return new TwoFactorAuthenticationException(
        "Invalid two-factor authentication code");
  }

  /**
   * Creates exception for expired 2FA code
   */
  public static TwoFactorAuthenticationException expiredCode() {
    return new TwoFactorAuthenticationException(
        "Two-factor authentication code has expired. Please request a new code");
  }

  /**
   * Creates exception for 2FA not enabled
   */
  public static TwoFactorAuthenticationException notEnabled() {
    return new TwoFactorAuthenticationException(
        "Two-factor authentication is not enabled for this account");
  }

  /**
   * Creates exception for 2FA already enabled
   */
  public static TwoFactorAuthenticationException alreadyEnabled() {
    return new TwoFactorAuthenticationException(
        "Two-factor authentication is already enabled for this account");
  }
}
