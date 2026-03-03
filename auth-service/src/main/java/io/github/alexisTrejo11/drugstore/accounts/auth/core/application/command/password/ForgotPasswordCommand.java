package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password;

/**
 * Command for initiating password reset flow.
 * Validates that email is provided and has basic format.
 */
public record ForgotPasswordCommand(String email) {
  public ForgotPasswordCommand {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email cannot be null or blank");
    }
    if (!email.contains("@")) {
      throw new IllegalArgumentException("Email must be a valid email address");
    }
  }
}
