package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.persitence;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis implementation of TokenRepository for managing authentication tokens
 * (two-factor, activation, password reset, etc.) - NOT for JWT tokens.
 * 
 * Tokens are automatically deleted from Redis when they expire via TTL.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository {
  private final RedisTemplate<String, Object> redisTemplate;

  // Key prefixes for different data structures
  private static final String TOKEN_PREFIX = "token:";
  private static final String USER_TOKENS_PREFIX = "user:tokens:";

  /**
   * Retrieves all active tokens for a specific user.
   * Automatically filters out expired tokens.
   * 
   * @param userId the user ID
   * @return list of active tokens
   */
  @Override
  public List<Token> getUserTokens(String userId) {
    if (userId == null || userId.isBlank()) {
      log.warn("Attempted to get tokens with null or blank user ID");
      return List.of();
    }

    try {
      String userTokensKey = USER_TOKENS_PREFIX + userId;
      Set<Object> tokenCodes = redisTemplate.opsForSet().members(userTokensKey);

      if (tokenCodes == null || tokenCodes.isEmpty()) {
        log.debug("No active tokens found for user: {}", userId);
        return List.of();
      }

      log.debug("Found {} token(s) for user: {}", tokenCodes.size(), userId);

      // Retrieve all tokens and filter out expired ones
      List<Token> activeTokens = tokenCodes.stream()
          .map(code -> {
            String tokenKey = TOKEN_PREFIX + code;
            return (Token) redisTemplate.opsForValue().get(tokenKey);
          })
          .filter(token -> token != null && !token.isExpired())
          .collect(Collectors.toList());

      log.debug("Retrieved {} active token(s) for user: {}", activeTokens.size(), userId);
      return activeTokens;

    } catch (Exception e) {
      log.error("Error retrieving tokens for user: {}", userId, e);
      return List.of();
    }
  }

  /**
   * Creates and stores a new token with automatic expiration.
   * Also maintains a set of token codes per user for easy retrieval.
   * 
   * @param token the token to create
   */
  @Override
  public void create(Token token) {
    if (token == null || token.code() == null) {
      log.warn("Attempted to create null token or token with null code");
      return;
    }

    if (token.claims() == null || token.claims().userId() == null) {
      log.warn("Attempted to create token without valid user claims");
      return;
    }

    try {
      String tokenKey = TOKEN_PREFIX + token.code();
      String userTokensKey = USER_TOKENS_PREFIX + token.claims().userId();

      Duration ttl = Duration.between(LocalDateTime.now(), token.expiresAt());

      if (ttl.isNegative() || ttl.isZero()) {
        log.warn("Token already expired: type={}, user={}",
            token.type(), token.claims().userId());
        return;
      }

      // Store the token object with TTL - will auto-delete when expired
      redisTemplate.opsForValue().set(tokenKey, token, ttl);

      // Add token code to user's token set
      redisTemplate.opsForSet().add(userTokensKey, token.code());
      redisTemplate.expire(userTokensKey, ttl);

      log.info("Token created: type={}, user={}, code={}, expires in {} seconds",
          token.type(),
          token.claims().userId(),
          maskToken(token.code()),
          ttl.getSeconds());

    } catch (Exception e) {
      log.error("Error creating token: type={}, user={}",
          token.type(),
          token.claims() != null ? token.claims().userId() : "unknown",
          e);
      throw new RuntimeException("Failed to create token in Redis", e);
    }
  }

  /**
   * Retrieves a token by its code.
   * 
   * @param tokenCode the token code
   * @return Optional containing the token if found and not expired
   */
  @Override
  public Optional<Token> get(String tokenCode) {
    if (tokenCode == null || tokenCode.isBlank()) {
      log.warn("Attempted to get token with null or blank code");
      return Optional.empty();
    }

    try {
      String tokenKey = TOKEN_PREFIX + tokenCode;
      Token token = (Token) redisTemplate.opsForValue().get(tokenKey);

      if (token == null) {
        log.debug("Token not found: {}", maskToken(tokenCode));
        return Optional.empty();
      }

      if (token.isExpired()) {
        log.debug("Token expired: {}", maskToken(tokenCode));
        // Clean up expired token
        delete(tokenCode);
        return Optional.empty();
      }

      log.debug("Token retrieved: type={}, code={}", token.type(), maskToken(tokenCode));
      return Optional.of(token);

    } catch (Exception e) {
      log.error("Error retrieving token: {}", maskToken(tokenCode), e);
      return Optional.empty();
    }
  }

  /**
   * Deletes a token by its code.
   * 
   * @param tokenCode the token code to delete
   */
  @Override
  public void delete(String tokenCode) {
    if (tokenCode == null || tokenCode.isBlank()) {
      log.warn("Attempted to delete token with null or blank code");
      return;
    }

    try {
      String tokenKey = TOKEN_PREFIX + tokenCode;

      // Get token to find user ID before deletion
      Token token = (Token) redisTemplate.opsForValue().get(tokenKey);

      if (token != null && token.claims() != null && token.claims().userId() != null) {
        String userTokensKey = USER_TOKENS_PREFIX + token.claims().userId();
        redisTemplate.opsForSet().remove(userTokensKey, tokenCode);
      }

      Boolean deleted = redisTemplate.delete(tokenKey);

      if (Boolean.TRUE.equals(deleted)) {
        log.info("Token deleted: {}", maskToken(tokenCode));
      } else {
        log.debug("Token not found for deletion: {}", maskToken(tokenCode));
      }

    } catch (Exception e) {
      log.error("Error deleting token: {}", maskToken(tokenCode), e);
      throw new RuntimeException("Failed to delete token from Redis", e);
    }
  }

  /**
   * Deletes a specific token for a specific user.
   * Provides additional security by verifying ownership.
   * 
   * @param tokenCode the token code to delete
   * @param userId    the user ID who owns the token
   */
  @Override
  public void delete(String tokenCode, String userId) {
    if (tokenCode == null || tokenCode.isBlank() || userId == null || userId.isBlank()) {
      log.warn("Attempted to delete token with null or blank code/userId");
      return;
    }

    try {
      String tokenKey = TOKEN_PREFIX + tokenCode;

      // Verify token belongs to the user
      Token token = (Token) redisTemplate.opsForValue().get(tokenKey);

      if (token == null) {
        log.debug("Token not found for deletion: {}", maskToken(tokenCode));
        return;
      }

      if (token.claims() == null || !userId.equals(token.claims().userId())) {
        log.warn("Token ownership verification failed for user: {}", userId);
        return;
      }

      // Delete token and remove from user's token set
      String userTokensKey = USER_TOKENS_PREFIX + userId;
      redisTemplate.opsForSet().remove(userTokensKey, tokenCode);
      redisTemplate.delete(tokenKey);

      log.info("Token deleted for user: {}, code: {}", userId, maskToken(tokenCode));

    } catch (Exception e) {
      log.error("Error deleting token for user: {}, code: {}",
          userId, maskToken(tokenCode), e);
      throw new RuntimeException("Failed to delete token from Redis", e);
    }
  }

  /**
   * Masks a token code for secure logging (shows only first and last 4
   * characters).
   * 
   * @param tokenCode the token code to mask
   * @return masked token string
   */
  private String maskToken(String tokenCode) {
    if (tokenCode == null || tokenCode.length() < 8) {
      return "***";
    }
    return tokenCode.substring(0, 4) + "..." + tokenCode.substring(tokenCode.length() - 4);
  }
}
