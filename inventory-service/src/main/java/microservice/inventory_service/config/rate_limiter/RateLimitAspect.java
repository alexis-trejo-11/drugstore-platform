package microservice.inventory_service.config.rate_limiter;

import jakarta.servlet.http.HttpServletRequest;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.exceptions.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final RedisRateLimiter rateLimiter;

    @Value("${app.rate-limit.default.max-requests:100}")
    private int defaultMaxRequests;

    @Value("${app.rate-limit.default.duration-seconds:60}")
    private int defaultDurationSeconds;

    @Around("@annotation(libs_kernel.config.rate_limit.RateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);


        int maxRequests = rateLimit.maxRequests() != -1 ?
                rateLimit.maxRequests() : defaultMaxRequests;
        int duration = rateLimit.duration() != -1 ?
                rateLimit.duration() : defaultDurationSeconds;

        Duration durationObj = Duration.ofSeconds(duration);
        if (rateLimit.durationUnit() != java.util.concurrent.TimeUnit.SECONDS) {
            durationObj = Duration.of(duration, rateLimit.durationUnit().toChronoUnit());
        }

        String key = buildRateLimitKey(rateLimit, joinPoint);

        if (!rateLimiter.isAllowed(key, maxRequests, durationObj)) {
            RedisRateLimiter.RateLimitInfo rateLimitInfo = rateLimiter.getRateLimitInfo(key, maxRequests);
            throw new RateLimitExceededException(
                    String.format("Rate limit exceeded. Try again in %d seconds", rateLimitInfo.getResetAfter())
            );
        }

        return joinPoint.proceed();
    }

    private String buildRateLimitKey(RateLimit rateLimit, ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = getCurrentRequest();
        String baseKey = "";

        switch (rateLimit.type()) {
            case IP_BASED:
                baseKey = "ip:" + getClientIp(request);
                break;
            case USER_BASED:
                baseKey = "user:" + getUserId(request);
                break;
            case API_KEY_BASED:
                baseKey = "apikey:" + getApiKey(request);
                break;
            case CUSTOM:
                baseKey = "custom";
                break;
        }

        String methodName = joinPoint.getSignature().toShortString();
        return String.format("method:%s:%s", baseKey, methodName);
    }

    private HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private String getUserId(HttpServletRequest request) {
        // TODO: Validate and extract user ID from request (e.g., from JWT or session)
        // Ej: return request.getHeader("X-User-ID");
        return "anonymous";
    }

    private String getApiKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        return apiKey != null ? apiKey : "no-key";
    }
}