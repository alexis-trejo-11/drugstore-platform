package io.github.alexisTrejo11.drugstore.inventories.config.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;


@Component
@Order(1)
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            // Request ID único
            String requestId = UUID.randomUUID().toString();
            MDC.put("requestId", requestId);

            // Path del request
            MDC.put("requestPath", httpRequest.getRequestURI());
            MDC.put("requestMethod", httpRequest.getMethod());

            String userId = extractUserId(httpRequest);
            if (userId != null) {
                MDC.put("userId", userId);
            }

            MDC.put("clientIp", getClientIp(httpRequest));

            chain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }

    private String extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        // ... extract userID ID from token or session
        return "";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}