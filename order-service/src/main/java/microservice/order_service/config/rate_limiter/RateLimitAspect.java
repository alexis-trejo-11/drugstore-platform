package microservice.order_service.config.rate_limiter;


import jakarta.servlet.http.HttpServletRequest;
import libs_kernel.config.RateLimit;
import libs_kernel.exceptions.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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

    @Around("@annotation(libs_kernel.config.RateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        String key = buildRateLimitKey(rateLimit.key(), joinPoint);
        Duration duration = Duration.ofSeconds(rateLimit.duration());

        if (!rateLimiter.isAllowed(key, rateLimit.maxRequests(), duration)) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }

        return joinPoint.proceed();
    }

    private String buildRateLimitKey(String customKey, ProceedingJoinPoint joinPoint) {
        if (!customKey.isEmpty()) {
            return customKey;
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String clientIp = getClientIp(request);
        String methodName = joinPoint.getSignature().toShortString();

        return clientIp + ":" + methodName;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
