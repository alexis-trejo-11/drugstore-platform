package microservice.store_service.config.rate_limiter;

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

    public RateLimitInfo getRateLimitInfo(String key, int maxRequests) {
        String redisKey = "rate_limit:" + key;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        Object currentObj = ops.get(redisKey);
        long current = switch (currentObj) {
            case null -> 0;
            case Integer i -> i.longValue();
            case Long l -> l;
            default -> Long.parseLong(currentObj.toString());
        };

        long remaining = Math.max(0, maxRequests - current);
        long resetAfter = getTTL(redisKey);

        return new RateLimitInfo(maxRequests, remaining, resetAfter);
    }

    private Long getTTL(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public void resetLimit(String key) {
        String redisKey = "rate_limit:" + key;
        redisTemplate.delete(redisKey);
    }

    public static class RateLimitInfo {
        private final long limit;
        private final long remaining;
        private final long resetAfter;

        public RateLimitInfo(long limit, long remaining, long resetAfter) {
            this.limit = limit;
            this.remaining = remaining;
            this.resetAfter = resetAfter;
        }

        public long getLimit() {
            return limit;
        }

        public long getRemaining() {
            return remaining;
        }

        public long getResetAfter() {
            return resetAfter;
        }
    }
}
