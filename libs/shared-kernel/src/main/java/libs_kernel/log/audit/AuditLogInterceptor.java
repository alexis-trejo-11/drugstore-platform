package libs_kernel.log.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class AuditLogInterceptor implements HandlerInterceptor {
    private final AuditLogger auditLogger;
    private final String serviceName;
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    private final Set<String> excludedEndpoints = Set.of(
            "/actuator/health",
            "/actuator/info",
            "/actuator/metrics",
            "/favicon.ico",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars",
            "/swagger-resources"
    );

    @Autowired
    public AuditLogInterceptor(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
        this.serviceName = System.getenv().getOrDefault("SPRING_APPLICATION_NAME", "unknown-service");
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (shouldSkipAudit(request)) {
            return true;
        }
        startTime.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        if (shouldSkipAudit(request)) return;

        Long start = startTime.get();
        if (start == null) return;

        long duration = System.currentTimeMillis() - start;

        try {
            AuditEvent event = AuditEvent.builder()
                    .serviceName(serviceName)
                    .method(request.getMethod())
                    .endpoint(sanitizeEndpoint(request.getRequestURI()))
                    .operation(extractOperation(request))
                    .userID(extractUserId(request))
                    .clientIP(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .statusCode(response.getStatus())
                    .durationMs(duration)
                    .success(ex == null && response.getStatus() < 400)
                    .metadata(buildMetadata(request, ex))
                    .build();

            auditLogger.logAuditEvent(event);

        } catch (Exception e) {
            log.warn("Error creating audit event", e);
        } finally {
            startTime.remove();
        }
    }


    private boolean shouldSkipAudit(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        for (String excluded : excludedEndpoints) {
            if (requestURI.startsWith(excluded)) {
                return true;
            }
        }

        // Exclude OPTIONS requests commonly used for CORS preflight
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }


    private String sanitizeEndpoint(String endpoint) {
        if (endpoint == null) return "";

        return endpoint.replaceAll("/\\d+", "/{id}")
                .replaceAll("/[0-9a-fA-F-]{36}", "/{uuid}")
                .replaceAll("/[A-Z0-9]{10,}", "/{code}");
    }

    private String extractOperation(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase();
        String endpoint = request.getRequestURI();

        if (endpoint.contains("/users")) {
            return switch (method) {
                case "GET" -> endpoint.matches(".*/\\{id\\}$") ? "GET_USER" : "LIST_USERS";
                case "POST" -> "CREATE_USER";
                case "PUT", "PATCH" -> "UPDATE_USER";
                case "DELETE" -> "DELETE_USER";
                default -> method + "_USER";
            };
        } else if (endpoint.contains("/orders")) {
            return switch (method) {
                case "GET" -> endpoint.matches(".*/\\{id\\}$") ? "GET_ORDER" : "LIST_ORDERS";
                case "POST" -> "CREATE_ORDER";
                case "PUT", "PATCH" -> "UPDATE_ORDER";
                case "DELETE" -> "DELETE_ORDER";
                default -> method + "_ORDER";
            };
        } else if (endpoint.contains("/products")) {
            return switch (method) {
                case "GET" -> endpoint.matches(".*/\\{id\\}$") ? "GET_PRODUCT" : "LIST_PRODUCTS";
                case "POST" -> "CREATE_PRODUCT";
                case "PUT", "PATCH" -> "UPDATE_PRODUCT";
                case "DELETE" -> "DELETE_PRODUCT";
                default -> method + "_PRODUCT";
            };
        }
        // TODO: Add mi entities paths

        // Generic response if no specific mapping found
        return method + "_" + endpoint.replace("/", "_")
                .replaceAll("[^a-zA-Z0-9_]", "")
                .toUpperCase();
    }


    private String extractUserId(HttpServletRequest request) {
        // 1.= From headers
        String userIdHeader = request.getHeader("X-User-ID");
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
            return userIdHeader;
        }

        // 2.= From Authorization header (e.g., JWT)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // In a real implementation, parse the JWT and extract the user ID claim
            // placeholder
            return "user-from-token";
        }

        // 3.= From request attributes (set by some auth middleware)
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr != null) {
            return userIdAttr.toString();
        }

        return "anonymous";
    }

    private String getClientIp(HttpServletRequest request) {
        String[] ipHeaders = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : ipHeaders) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // In this case, X-Forwarded-For can contain multiple IPs, the first is taken
                if (header.equals("X-Forwarded-For")) {
                    return ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        // Fallback to remote address
        return request.getRemoteAddr();
    }


    private Map<String, Object> buildMetadata(HttpServletRequest request, Exception ex) {
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("queryString", request.getQueryString());
        metadata.put("contentType", request.getContentType());
        metadata.put("serverName", request.getServerName());
        metadata.put("serverPort", request.getServerPort());
        metadata.put("locale", request.getLocale().toString());

        metadata.put("accept", request.getHeader("Accept"));
        metadata.put("acceptLanguage", request.getHeader("Accept-Language"));
        metadata.put("acceptEncoding", request.getHeader("Accept-Encoding"));

        if (request.getSession(false) != null) {
            metadata.put("sessionId", request.getSession().getId());
        }

        if (ex != null) {
            metadata.put("errorType", ex.getClass().getSimpleName());
            metadata.put("errorMessage", ex.getMessage());
            // Stacktrace limited to the first element for brevity
            StackTraceElement[] stackTrace = ex.getStackTrace();
            if (stackTrace.length > 0) {
                metadata.put("errorLocation", stackTrace[0].toString());
            }
        }

        return metadata;
    }
}