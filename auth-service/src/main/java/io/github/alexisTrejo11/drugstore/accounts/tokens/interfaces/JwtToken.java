package io.github.alexisTrejo11.drugstore.accounts.tokens.interfaces;

import io.jsonwebtoken.Claims;

public interface JwtToken extends Token {
  Claims getClaims();

  String getSubject();
}
