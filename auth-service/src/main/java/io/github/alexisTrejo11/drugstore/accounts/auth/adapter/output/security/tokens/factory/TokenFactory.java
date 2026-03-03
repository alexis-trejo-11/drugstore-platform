package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.factory;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import libs_kernel.security.dto.UserClaims;
import libs_kernel.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;

import java.nio.charset.StandardCharsets;

/**
 * Centralized factory for creating all types of tokens.
 * Returns concrete Token records (not interfaces).
 * All token creation flows through this factory.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFactory {
  private final JwtProperties jwtProperties;
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  @Value("${token.activation.length:6}")
  private int activationTokenLength;

  @Value("${token.two-fa.length:6}")
  private int twoFaTokenLength;

  @Value("${token.activation.expiration-minutes:15}")
  private int activationExpirationMinutes;

  @Value("${token.two-fa.expiration-minutes:5}")
  private int twoFaExpirationMinutes;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Create a token based on type and user claims.
   * Returns a concrete Token record.
   * 
   * @param type       the token type
   * @param userClaims the user claims
   * @return Token record
   */
  public Token createToken(TokenType type, UserClaims userClaims) {
    return switch (type) {
      case ACCESS -> createAccessToken(userClaims);
      case REFRESH -> createRefreshToken(userClaims);
      case ACTIVATION -> createActivationToken(userClaims);
      case TWO_FA -> createTwoFaToken(userClaims);
    };
  }

  /**
   * Creates an access JWT token.
   */
  private Token createAccessToken(UserClaims userClaims) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", userClaims.role());
    claims.put("userId", userClaims.userId());
    claims.put("email", userClaims.email());
    claims.put("name", userClaims.name());
    claims.put("phoneNumber", userClaims.phoneNumber());
    claims.put("type", "access");

    long expirationMs = jwtProperties.getAccessTokenExpirationSeconds();
    LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationMs / 1000);

    String tokenCode = Jwts.builder()
        .setClaims(claims)
        .setSubject(userClaims.userId())
        .setIssuer(jwtProperties.getIssuer())
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(Date.from(Instant.now().plusMillis(expirationMs)))
        .signWith(getSigningKey())
        .compact();

    log.debug("Created ACCESS token for user: {}", userClaims.userId());

    return new Token(
        tokenCode,
        "ACCESS",
        Duration.ofMillis(expirationMs),
        expiresAt,
        userClaims);
  }

  /**
   * Creates a refresh JWT token.
   */
  private Token createRefreshToken(UserClaims userClaims) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userClaims.userId());
    claims.put("type", "refresh");

    long expirationMs = jwtProperties.getRefreshTokenExpirationSeconds();
    LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationMs / 1000);

    String tokenCode = Jwts.builder()
        .setClaims(claims)
        .setSubject(userClaims.userId())
        .setIssuer(jwtProperties.getIssuer())
        .setIssuedAt(Date.from(Instant.now()))
        .setExpiration(Date.from(Instant.now().plusMillis(expirationMs)))
        .signWith(getSigningKey())
        .compact();

    log.debug("Created REFRESH token for user: {}", userClaims.userId());

    return new Token(
        tokenCode,
        "REFRESH",
        Duration.ofMillis(expirationMs),
        expiresAt,
        userClaims);
  }

  /**
   * Creates a numeric activation token.
   */
  private Token createActivationToken(UserClaims userClaims) {
    String tokenCode = generateNumericToken(activationTokenLength);
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(activationExpirationMinutes);

    log.debug("Created ACTIVATION token for user: {}", userClaims.userId());

    return new Token(
        tokenCode,
        "ACTIVATION",
        Duration.ofMinutes(activationExpirationMinutes),
        expiresAt,
        userClaims);
  }

  /**
   * Creates a numeric two-factor authentication token.
   */
  private Token createTwoFaToken(UserClaims userClaims) {
    String tokenCode = generateNumericToken(twoFaTokenLength);
    LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(twoFaExpirationMinutes);

    log.debug("Created TWO_FA token for user: {}", userClaims.userId());

    return new Token(
        tokenCode,
        "TWO_FA",
        Duration.ofMinutes(twoFaExpirationMinutes),
        expiresAt,
        userClaims);
  }

  /**
   * Validates a JWT token.
   */
  public boolean validateJwtToken(String tokenCode) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(tokenCode);
      return true;
    } catch (Exception e) {
      log.debug("JWT token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Validates a numeric token (activation or 2FA).
   */
  public boolean validateNumericToken(String tokenCode, int expectedLength) {
    if (tokenCode == null || tokenCode.length() != expectedLength) {
      return false;
    }
    for (char c : tokenCode.toCharArray()) {
      if (!Character.isDigit(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Extracts user claims from a JWT token.
   */
  public UserClaims extractClaimsFromJwt(String tokenCode) {
    try {
      Claims claims = Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(tokenCode)
          .getBody();

      return UserClaims.builder()
          .userId(claims.get("userId", String.class))
          .email(claims.get("email", String.class))
          .name(claims.get("name", String.class))
          .role(claims.get("role", String.class))
          .phoneNumber(claims.get("phoneNumber", String.class))
          .build();
    } catch (Exception e) {
      log.error("Failed to extract claims from JWT: {}", e.getMessage());
      throw new RuntimeException("Invalid JWT token", e);
    }
  }

  /**
   * Generates a secure random numeric token.
   */
  private String generateNumericToken(int length) {
    int min = (int) Math.pow(10, length - 1);
    int max = (int) Math.pow(10, length) - 1;
    return String.valueOf(min + SECURE_RANDOM.nextInt(max - min + 1));
  }
}
