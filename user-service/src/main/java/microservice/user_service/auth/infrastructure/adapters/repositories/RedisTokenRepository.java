package microservice.user_service.auth.infrastructure.adapters.repositories;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.user_service.auth.core.ports.output.TokenRepository;
import microservice.user_service.utils.tokens.interfaces.TokenType;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisTokenRepository implements TokenRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_KEY_PREFIX = "token:";
    private static final String USER_TOKENS_KEY_PREFIX = "user_tokens:";

    public void save(String token, String userId, long expiresInMillis, TokenType tokenType) {
        try {
            String tokenKey = getTokenKey(token);
            String userTokensKey = getUserTokensKey(userId);
            Map<String, Object> tokenData = Map.of(
                    "token", token,
                    "userId", userId,
                    "token_type", tokenType.name());

            redisTemplate.opsForValue().set(
                    tokenKey,
                    tokenData,
                    Duration.ofMillis(expiresInMillis));
            redisTemplate.opsForSet().add(userTokensKey, token);

            redisTemplate.expire(userTokensKey, 30, TimeUnit.DAYS);

            log.debug("Token saved for user: {}", userId);
        } catch (Exception e) {
            log.error("Error saving token for user: {}", userId, e);
            throw new RuntimeException("Failed to save token", e);
        }
    }

    public boolean isValid(String token) {
        try {
            String tokenKey = getTokenKey(token);
            return redisTemplate.hasKey(tokenKey);
        } catch (Exception e) {
            log.error("Error checking token validity: {}", token, e);
            throw new RuntimeException("Failed to check token validity", e);
        }
    }

    public void invalidate(String token, String userId) {
        try {
            String tokenKey = getTokenKey(token);
            String userTokensKey = getUserTokensKey(userId);

            redisTemplate.delete(tokenKey);
            redisTemplate.opsForSet().remove(userTokensKey, token);

            log.debug("Token invalidated for user: {}", userId);
        } catch (Exception e) {
            log.error("Error invalidating token for user: {}", userId, e);
            throw new RuntimeException("Failed to invalidate token", e);
        }
    }

    private String getTokenKey(String token) {
        return TOKEN_KEY_PREFIX + token;
    }

    private String getUserTokensKey(String userId) {
        return USER_TOKENS_KEY_PREFIX + userId;
    }
}
