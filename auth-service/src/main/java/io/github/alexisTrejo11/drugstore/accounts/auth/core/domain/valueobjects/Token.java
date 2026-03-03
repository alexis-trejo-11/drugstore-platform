package io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects;

import libs_kernel.security.dto.UserClaims;

import java.time.Duration;
import java.time.LocalDateTime;

public record Token(
    String code,
    String type,
    Duration expiresIn,
    LocalDateTime expiresAt,
    UserClaims claims) {
  public Token {
    if (code == null || type == null || expiresIn == null || expiresAt == null) {
      throw new IllegalArgumentException("Token, type, expiresIn, and expiresAt cannot be null");
    }
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }
}
