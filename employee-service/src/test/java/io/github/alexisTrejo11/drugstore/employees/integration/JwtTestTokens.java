package io.github.alexisTrejo11.drugstore.employees.integration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Builds JWTs using the same HMAC secret as {@code application-test.yml} so requests hit the real
 * {@link libs_kernel.security.jwt.JwtAuthenticationFilter} stack.
 */
public final class JwtTestTokens {

  private JwtTestTokens() {}

  public static String accessToken(String secret, String userId, String role) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
        .setId(UUID.randomUUID().toString())
        .claim("userId", userId)
        .claim("role", role)
        .claim("type", "access")
        .claim("email", userId + "@integration.test")
        .signWith(key)
        .compact();
  }
}
