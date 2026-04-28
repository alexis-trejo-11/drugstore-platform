package io.github.alexisTrejo11.drugstore.products.config.rate_limiter;

import java.time.Duration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.exceptions.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

  private final RedisRateLimiter rateLimiter;
  private final RateLimitProperties properties;

  @Around("@annotation(rateLimit)")
  public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
    RateLimitProfile profile = rateLimit.profile();

    RateLimitProperties.ProfileConfig config = properties.getProfiles()
        .getOrDefault(profile.name().toLowerCase(), getDefaultConfig());

    // Set final values (Priority: Annotation > YAML)
    int maxRequests = rateLimit.maxRequests() != -1
        ? rateLimit.maxRequests()
        : config.getMaxRequests();

    long durationSecs = rateLimit.duration() != -1
        ? rateLimit.durationUnit().toSeconds(rateLimit.duration())
        : config.getDurationSeconds();

    String key = buildRateLimitKey(rateLimit, joinPoint);

    log.info("Rate Limit Check - Key: {}, Max: {}, Duration: {}s", key, maxRequests, durationSecs);

    if (!rateLimiter.isAllowed(key, maxRequests, Duration.ofSeconds(durationSecs))) {
      log.warn("Rate limit exceeded for key: {}", key);
      // Add rate limit info to response headers even when limit is exceeded
      addRateLimitHeaders(key, maxRequests);
      throw new RateLimitExceededException("Limit exceeded for profile: " + profile);
    }

    // Add rate limit info to response headers
    addRateLimitHeaders(key, maxRequests);

    log.info("Rate limit check passed for key: {}", key);

    return joinPoint.proceed();
  }

  private void addRateLimitHeaders(String key, int maxRequests) {
    try {
      HttpServletResponse response = getCurrentResponse();
      if (response != null) {
        RedisRateLimiter.RateLimitInfo info = rateLimiter.getRateLimitInfo(key, maxRequests);

        response.setHeader("X-RateLimit-Limit", String.valueOf(info.getLimit()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(info.getRemaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(info.getResetAfter()));

        log.debug("Rate limit headers added - Limit: {}, Remaining: {}, Reset: {}s",
            info.getLimit(), info.getRemaining(), info.getResetAfter());
      }
    } catch (Exception e) {
      log.error("Error adding rate limit headers", e);
    }
  }

  private String buildRateLimitKey(RateLimit rateLimit, ProceedingJoinPoint joinPoint) {
    if (!rateLimit.key().isEmpty()) {
      return "manual:" + rateLimit.key();
    }

    HttpServletRequest request = getCurrentRequest();

    String subjectIdentifier = switch (rateLimit.type()) {
      case IP_BASED -> getClientIp(request);
      case USER_BASED -> getUserId(request);
      case API_KEY_BASED -> getApiKey(request);
      case CUSTOM -> "custom";
    };

    String actionIdentifier = joinPoint.getSignature().toShortString();

    return String.format("profile:%s:%s:%s:%s",
        rateLimit.profile().name().toLowerCase(),
        rateLimit.type().name().toLowerCase(),
        subjectIdentifier,
        actionIdentifier);
  }

  private RateLimitProperties.ProfileConfig getDefaultConfig() {
    RateLimitProperties.ProfileConfig config = new RateLimitProperties.ProfileConfig();
    config.setMaxRequests(100);
    config.setDurationSeconds(60);
    return config;
  }

  private HttpServletRequest getCurrentRequest() {
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    return attrs.getRequest();
  }

  private HttpServletResponse getCurrentResponse() {
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attrs != null ? attrs.getResponse() : null;
  }

  private String getClientIp(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader != null && !xfHeader.isEmpty()) {
      return xfHeader.split(",")[0];
    }
    return request.getRemoteAddr();
  }

  private String getUserId(HttpServletRequest request) {
    // In future extract this from SecurityContextHolder or JWT Header
    String userId = request.getHeader("X-User-Id");
    return userId != null ? userId : "anonymous";
  }

  private String getApiKey(HttpServletRequest request) {
    String apiKey = request.getHeader("X-API-Key");
    return apiKey != null ? apiKey : "no-key";
  }
}
