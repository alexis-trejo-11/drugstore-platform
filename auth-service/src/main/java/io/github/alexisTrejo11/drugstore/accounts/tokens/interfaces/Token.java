package io.github.alexisTrejo11.drugstore.accounts.tokens.interfaces;

import java.time.LocalDateTime;

public interface Token {
  String generate();

  boolean validate(String token);

  String getType();

  LocalDateTime expiresAt();
}
