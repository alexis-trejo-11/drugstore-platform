package microservice.inventory_service.config.rate_limiter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisRateLimiter {
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isAllowed(String key, int maxRequests, Duration duration) {
        String redisKey = "rate_limit:" + key;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Long current = ops.increment(redisKey);

        if (current == null) {
            return false;
        }

        if (current == 1) {
            redisTemplate.expire(redisKey, duration.toSeconds(), TimeUnit.SECONDS);
        }

        return current <= maxRequests;
    }

    public long getRemainingRequests(String key, int maxRequests) {
        String redisKey = "rate_limit:" + key;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Long current = (Long) ops.get(redisKey);

        if (current == null) {
            return maxRequests;
        }

        return Math.max(0, maxRequests - current);
    }

    public void resetLimit(String key) {
        String redisKey = "rate_limit:" + key;
        redisTemplate.delete(redisKey);
    }
}
