package io.github.alexisTrejo11.drugstore.accounts.tokens.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.tokens.interfaces.Token;
import io.github.alexisTrejo11.drugstore.accounts.tokens.interfaces.TokenType;

@Component
@RequiredArgsConstructor
public class TokenFactory {
  private final AccessToken accessToken;
  private final RefreshToken refreshToken;
  private final ActivationToken activationToken;
  private final TwoFaToken twoFAToken;

  public Token createToken(TokenType type, String subject, Map<String, Object> claims) {
    return switch (type) {
      case ACCESS -> {
        accessToken.setSubject(subject);
        accessToken.setClaims(claims != null ? claims : Map.of());
        yield accessToken;
      }
      case REFRESH -> {
        refreshToken.setSubject(subject);
        refreshToken.setClaims(claims != null ? claims : Map.of());
        yield refreshToken;
      }
      case ACTIVATION -> {
        activationToken.setEmail((String) claims.get("email"));
        yield activationToken;
      }
      case TWO_FA -> twoFAToken;
    };
  }

  public Token createToken(TokenType type) {
    return createToken(type, null, Map.of());
  }
}
