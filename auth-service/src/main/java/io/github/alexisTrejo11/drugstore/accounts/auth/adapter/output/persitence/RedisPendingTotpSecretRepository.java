package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.persitence;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

/**
 * Temporary storage for TOTP secrets during 2FA enrollment (until user confirms with a valid code).
 */
@Repository
@RequiredArgsConstructor
public class RedisPendingTotpSecretRepository {
  private static final String PREFIX = "auth:2fa:pending:";
  private final StringRedisTemplate stringRedisTemplate;

  public void put(String userId, String secretBase32, Duration ttl) {
    stringRedisTemplate.opsForValue().set(PREFIX + userId, secretBase32, ttl);
  }

  public Optional<String> get(String userId) {
    String v = stringRedisTemplate.opsForValue().get(PREFIX + userId);
    return Optional.ofNullable(v);
  }

  public void delete(String userId) {
    stringRedisTemplate.delete(PREFIX + userId);
  }
}
