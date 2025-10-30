package microservice.inventory_service.config.rate_limiter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1)
@RequiredArgsConstructor
public class GlobalRateLimitFilter implements Filter {

    private final RedisRateLimiter rateLimiter;
    private final RateLimitProperties properties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!properties.getGlobal().isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Excluir endpoints de monitoreo y documentación
        String path = httpRequest.getRequestURI();
        if (shouldExcludeFromGlobalLimit(path)) {
            chain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(httpRequest);
        String globalKey = "global:ip:" + clientIp;
        Duration duration = Duration.ofHours(properties.getGlobal().getDurationHours());
        int maxRequests = properties.getGlobal().getMaxRequests();

        RedisRateLimiter.RateLimitInfo rateLimitInfo = rateLimiter.getRateLimitInfo(globalKey, maxRequests);
        boolean isAllowed = rateLimiter.isAllowed(globalKey, maxRequests, duration);

        addRateLimitHeaders(httpResponse, rateLimitInfo, maxRequests, duration);

        if (!isAllowed) {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\": \"Global rate limit exceeded\", \"code\": \"RATE_LIMIT_GLOBAL\"}"
            );
            return;
        }

        chain.doFilter(request, response);
    }

    private void addRateLimitHeaders(HttpServletResponse response,
                                     RedisRateLimiter.RateLimitInfo rateLimitInfo,
                                     int maxRequests,
                                     Duration duration) {

        response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(rateLimitInfo.getRemaining()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(rateLimitInfo.getResetAfter()));

        response.setHeader("X-RateLimit-Policy",
                String.format("%d;w=%d", maxRequests, duration.toSeconds()));
        response.setHeader("X-RateLimit-Duration-Hours",
                String.valueOf(properties.getGlobal().getDurationHours()));

        response.setHeader("X-RateLimit-Scope", "global");

        if (rateLimitInfo.getRemaining() == 0) {
            response.setHeader("Retry-After", String.valueOf(rateLimitInfo.getResetAfter()));
        }
    }

    private boolean shouldExcludeFromGlobalLimit(String path) {
        return path.startsWith("/actuator/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/health") ||
                path.equals("/favicon.ico");
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null) {
            return xfHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}